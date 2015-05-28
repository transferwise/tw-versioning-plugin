class TwVersioningPluginGrailsPlugin {
    // the plugin version
    def version = '1.2'
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def groupId = "com.transferwise.grails-plugins"
}
