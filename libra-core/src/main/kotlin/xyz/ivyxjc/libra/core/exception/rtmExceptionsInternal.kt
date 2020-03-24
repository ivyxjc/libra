package xyz.ivyxjc.libra.core.exception

class LibraMissingConfigException : RuntimeException {
    /**
     * Constructs a LibraMissingConfigException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param configName the name of the config
     * @param key the key for the missing resource.
     */
    constructor(s: String, configName: String, key: String) : super(s) {
        this.configName = configName
        this.key = key
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the name of the resource class
     */
    fun getConfigName(): String? {
        return configName
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the key for the missing resource
     */
    fun getKey(): String? {
        return key
    }

    //============ privates ============

    // serialization compatibility with JDK1.1
    private val serialVersionUID = -4876345176062000411L

    /**
     * The class name of the resource bundle requested by the user.
     * @serial
     */
    private var configName: String? = null

    /**
     * The name of the specific resource requested by the user.
     * @serial
     */
    private var key: String? = null
}

class LibraConfigConflictException : RuntimeException {


    /**
     * Constructs a LibraConfigConflictException with the specified information.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     * @param configName the name of the config
     * @param key the key for the conflicted resource.
     */
    constructor(s: String, configName: String, key: String) : super(s) {
        this.configName = configName
        this.key = key
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the name of the resource class
     */
    fun getConfigName(): String? {
        return configName
    }

    /**
     * Gets parameter passed by constructor.
     *
     * @return the key for the missing resource
     */
    fun getKey(): String? {
        return key
    }

    //============ privates ============

    // serialization compatibility with JDK1.1
    private val serialVersionUID = -4876345176062000411L

    /**
     * The class name of the resource bundle requested by the user.
     * @serial
     */
    private var configName: String? = null

    /**
     * The name of the specific resource requested by the user.
     * @serial
     */
    private var key: String? = null
}