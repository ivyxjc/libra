package xyz.ivyxjc.libra.core.service

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.Yaml
import xyz.ivyxjc.libra.core.StartupInit
import xyz.ivyxjc.libra.core.models.Remediation
import xyz.ivyxjc.libra.core.models.SourceConfig
import xyz.ivyxjc.libra.core.models.Transformation
import xyz.ivyxjc.libra.core.process.LibraProcessor


interface SourceConfigService {
    fun getSourceConfig(sourceId: Long): SourceConfig?
}

@Service
class SourceConfigServiceMockImpl : SourceConfigService, ApplicationContextAware, InitializingBean {

    private var sourceMap = mutableMapOf<Long, SourceConfig>()
    private lateinit var conetxt: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.conetxt = applicationContext
    }

    override fun afterPropertiesSet() {
        val yaml = Yaml()
        val input = StartupInit::class.java.classLoader.getResourceAsStream("source-config.yaml")
        val sourceConfig = SourceConfig()
        input.use {
            val map: Map<*, Map<*, *>> = yaml.load(input)
            println("++++++++++++++++++")
            map.forEach { k, v ->
                println(k)
                val transProcessor = mutableListOf<LibraProcessor>()
                val remeProcessor = mutableListOf<LibraProcessor>()
                sourceConfig.sourceId = k.toString().toLong()
                val queue = v["transformationQueue"] as? String
                val trans = v["transformation"] as? String
                val remes = v["remediation"] as? String
                val usecases = v["usecase"] as? String
                val status = v["status"] as? String
                if (StringUtils.isNotBlank(trans)) {
                    trans!!.split(",").forEach {
                        val tmp = it.trim()
                        transProcessor.add(conetxt.getBean(tmp) as LibraProcessor)
                    }
                }
                if (StringUtils.isNotBlank(remes)) {
                    remes?.split(",")?.forEach {
                        val tmp = it.trim()
                        remeProcessor.add(conetxt.getBean(tmp) as LibraProcessor)
                    }
                }
                sourceConfig.usecases = usecases?.split(",")?.toList()
                sourceConfig.status = status?.split(",")?.toList()
                sourceConfig.transformationQueue = queue
                sourceConfig.transformation = Transformation(transProcessor.toList())
                sourceConfig.remediation = Remediation(remeProcessor.toList())
                sourceMap[sourceConfig.sourceId] = sourceConfig
            }
        }


    }


    override fun getSourceConfig(sourceId: Long): SourceConfig? {
        return sourceMap[sourceId]
    }
}