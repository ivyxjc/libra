package xyz.ivyxjc.libra.starter.jms.model.inner

internal class LibraJmsListenerYaml {
    lateinit var destination: String
    lateinit var messageListener: String
    lateinit var dispatcher: String

    var sourceIds: String = "ALL"
    var id: String = ""
    var containerFactory: String = ""
    var subscription: String = ""
    var selector: String = ""
    var concurrency: String = ""

}