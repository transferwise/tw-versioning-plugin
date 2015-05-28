includeTargets << grailsScript("_GrailsInit")

target(branchVersion: "Adds branch qualifier to version") {

	String branch = argsMap.branch

	if (branch.contains("master")) {
		println "Master does not need qualifier"
		return
	}

	branch = branch.split("/").last()

	setAppVersion(addQualifier(branch))
}

private String getReleaseVersion() {
	def version = getAppVersion().replace('-SNAPSHOT', '')
	println 'Current version is ' + version
	return version
}

private String addQualifier(String branch) {
	"${getReleaseVersion()}-$branch-SNAPSHOT"
}

private String getAppVersion() {

	def version

	try {

		def directory = new File("${basedir}/")

		if (directory?.isDirectory()) {

			def filePattern = ~/^.+GrailsPlugin.groovy$/

			def fileMatches = directory.list( [accept:{d, f-> f ==~ /^.+GrailsPlugin.groovy$/ }] as FilenameFilter)?.toList()

			if (fileMatches?.size() > 0) {

				def pluginDescriptor = "${basedir}/${fileMatches[0]}" as File

				def captured = ''

				pluginDescriptor.getText().find(/def version\s?=\s?[\"|\']?(.*)[\"|\']/) { match, capture -> captured = capture }

				if (captured) version = captured

			}

		}

		if (!version) {

			def applicationPropertiesFile = "${basedir}/application.properties" as File
			def applicationProperties = new Properties()
			applicationProperties.load(applicationPropertiesFile.newReader())
			version = applicationProperties['app.version']

		}

	} catch (Exception ex) {
		version = null
	}

	return version

}

private Boolean setAppVersion(version) {

	Boolean result

	try {

		def directory = new File("${basedir}/")

		if (directory?.isDirectory()) {

			def filePattern = ~/^.+GrailsPlugin.groovy$/

			def fileMatches = directory.list( [accept:{d, f-> f ==~ /^.+GrailsPlugin.groovy$/ }] as FilenameFilter)?.toList()

			if (fileMatches?.size() > 0) {

				def pluginDescriptor = "${basedir}/${fileMatches[0]}" as File

				String content = pluginDescriptor.getText()

				content = content.replaceFirst(~/def version\s?=\s?[\"|\']?(.*)[\"|\']/) {
					"def version = \'${version}\'"
				}

				pluginDescriptor.write(content)

				result = true

			}

		}

		if (!result) {

			def applicationPropertiesFile = "${basedir}/application.properties" as File
			def props = new Properties()
			props.load(new FileInputStream(applicationPropertiesFile))
			props['app.version'] = version.toString()
			props.store(new FileOutputStream(applicationPropertiesFile), 'Grails Metadata file')
			result = true

		}

	} catch (Exception ex) {
		result = false
	}

	return result

}

setDefaultTarget(branchVersion)
