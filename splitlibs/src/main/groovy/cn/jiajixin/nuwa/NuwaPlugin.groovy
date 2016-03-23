package cn.jiajixin.nuwa
import cn.jiajixin.nuwa.util.NuwaAndroidUtils
import cn.jiajixin.nuwa.util.NuwaFileUtils
import cn.jiajixin.nuwa.util.NuwaMapUtils
import cn.jiajixin.nuwa.util.NuwaProcessor
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class NuwaPlugin implements Plugin<Project> {
    HashSet<String> includePackage = []
    HashSet<String> excludePackage = []
    HashSet<String> excludeClass = []
    def debugOn
    def lastBuildDir
    def patchFilePrefixName
    private static final String NUWA_DIR = "NuwaDir"

    private static final String MAPPING_TXT = "mapping.txt"
    private static final String HASH_TXT = "hash.txt"

    private static final String RELEASE = "Release"


    @Override
    void apply(Project project) {

//        def isApp = project.plugins.hasPlugin(AppPlugin)
//        if (isApp) {

        println("extensions:" + project.extensions)
//            def android = project.extensions.getByType(AppExtension)
//            def transform = new TransformImpl(project)
//            android.registerTransform(transform)
////        }
//
//        project.extensions.create("nuwa", NuwaExtension, project)
//
//        project.afterEvaluate {
//            def extension = project.extensions.findByName("nuwa") as NuwaExtension
//            debugOn = extension.debugOn
//            patchFilePrefixName = extension.patchFilePrefixName
//
//            ////initial include classes and exclude classes
//            extension.includePackage.each {
//                includeName ->
//                    includePackage.add(includeName.replace(".", "/"))
//            }
//
//            extension.excludePackage.add("android.support")
//            extension.excludePackage.each {
//                excludeName ->
//                    excludePackage.add(excludeName.replace(".", "/"))
//            }
//            extension.excludeClass.each {
//                excludeName ->
//                    excludeClass.add(excludeName.replace(".", "/") + ".class")
//            }
//
//            println("include:" + includePackage)
//            println("exclude:" + excludePackage)
//            println("excludeClass:" + excludeClass)
//
//            ////intial last build nuwa dir
//            lastBuildDir = NuwaFileUtils.getFileFromProperty(project, NUWA_DIR)
//            if (!lastBuildDir) {
//                lastBuildDir = NuwaFileUtils.getFileDir(extension.patchPath)
//            }
//            println("lastBuildDir:" + lastBuildDir)
//
//            ////
//
//
//            project.android.applicationVariants.each { variant ->
//                println("variant:" + variant.name)
//                println("variant:" + variant.getDirName())
//
//                def proguardTask = project.tasks.findByName("transformClassesAndResourcesWithProguardFor${variant.name.capitalize()}")
//                println "proguardTask is ${proguardTask}";
//                if (proguardTask) {
//                    project.logger.error "proguard=>${variant.name.capitalize()}"
//
//                    proguardTask.inputs.files.files.each { File file->
//                        project.logger.error "file inputs=>${file.absolutePath}"
//                    }
//
//                    proguardTask.outputs.files.files.each { File file->
//                        project.logger.error "file outputs=>${file.absolutePath}"
//                    }
//                }
//
//                def dexTask1 = project.tasks.findByName("transformClassesWithDexFor${variant.name.capitalize()}")
//                println "dexTask is ${dexTask1}";
//
//
//                if (dexTask1) {
//                    project.logger.error "dex=>${variant.name.capitalize()}"
//
//                    println "dexTask.inputs is ${dexTask1.inputs}";
//                    println "dexTask.inputs.files is ${dexTask1.inputs.files}";
//                    println "dexTask.inputs.files.files is ${dexTask1.inputs.files.files}";
//                    dexTask1.inputs.files.files.each { File file->
//                        println "file is ${file}";
//                        project.logger.error "file inputs=>${file.absolutePath}"
//                    }
//
//                    dexTask1.outputs.files.files.each { File file->
//                        project.logger.error "file outputs=>${file.absolutePath}"
//                    }
//                }
//
//                def testTask = project.tasks.findByName("transformClassesWithTransformImplFor${variant.name.capitalize()}")
//                println "testTask is ${testTask}";
//                if (testTask) {
//
//                    Set<File> testTaskInputFiles = testTask.inputs.files.files
//                    Set<File> testTaskOutputFiles = testTask.inputs.files.files
//
//                    project.logger.error "Name:transformClassesWithTransformImpl=====>${testTask.name} input"
//                    testTaskInputFiles.each { inputFile ->
//                        def path = inputFile.absolutePath
//                        project.logger.error path
//                    }
//
//                    project.logger.error "Name:transformClassesWithTransformImpl=====>${testTask.name} output"
//                    testTaskOutputFiles.each { inputFile ->
//                        def path = inputFile.absolutePath
//                        project.logger.error path
//                    }
//                }
//
//                if (variant.name.endsWith(RELEASE) || debugOn) {
//
//                    ///////
//                    def prepareTaskName = "check${variant.name.capitalize()}Manifest";
//                    def prepareTask = project.tasks.findByName(prepareTaskName)
//                    if (prepareTask) {
//                        prepareTask.doFirst({
//                            prepareBuild(project, variant);
//                        })
//                    } else {
//                        println("not found task ${prepareTaskName}")
//                    }
//
//                    ///////
//                    def dexTaskName = "transformClassesWithDexFor${variant.name.capitalize()}"
//                    def dexTask = project.tasks.findByName(dexTaskName)
//                    if (dexTask) {
//                        def patchProcessBeforeDex = "patchProcessBeforeDex${variant.name.capitalize()}"
//                        project.task(patchProcessBeforeDex) << {
//                            patchProcess(project, variant, dexTask);
//                        }
//                        //insert task
//                        def patchProcessBeforeDexTask = project.tasks[patchProcessBeforeDex]
//                        patchProcessBeforeDexTask.dependsOn dexTask.taskDependencies.getDependencies(dexTask)
//                        dexTask.dependsOn patchProcessBeforeDexTask
//                    } else {
//                        println("not found task:${dexTaskName}")
//                    }
//
//                }
//            }
//
//        }
    }

    void prepareBuild(def project, def variant) {
        //proguard map
        println("prepareBuild")

        //sign hack.apk
        def projectDir = project.projectDir.absolutePath
        projectDir = projectDir.subSequence(0, projectDir.lastIndexOf('/'));
        def hackApkFile = new File(projectDir + '/extras/hack.apk');
        def hackAssestApkFile = new File(projectDir + '/nuwa/src/main/assets/hack.apk');

        FileUtils.copyFile(hackApkFile, hackAssestApkFile)
        NuwaAndroidUtils.signedApk(variant, hackAssestApkFile)

        //apply mapping file
        File mappingProguardFile = new File(project.projectDir, "proguard-mapping.pro")
        mappingProguardFile.delete()
        mappingProguardFile.createNewFile()
        if (lastBuildDir) {
            def mappingFile = NuwaFileUtils.getVariantFile(lastBuildDir, variant, MAPPING_TXT)
            NuwaAndroidUtils.applymapping(mappingProguardFile, mappingFile)
        }

    }

    void patchProcess(def project, def variant, def dexTask) {
        println("nuwaJarBeforeDex")

        def dirName = variant.dirName
        def nuwaDir = new File("${project.buildDir}/outputs/nuwa")
        def outputDir = new File("${nuwaDir}/${dirName}")
//        FileUtils.deleteDirectory(outputDir)
        outputDir.mkdirs()


        //load last build class hash file
        Map hashMap
        def patchDir
        if (lastBuildDir) {
            patchDir = new File("${outputDir}/patch")
            patchDir.mkdirs()
            def oldhashFile = NuwaFileUtils.getVariantFile(lastBuildDir, variant, HASH_TXT)
            hashMap = NuwaMapUtils.parseMap(oldhashFile)
        }

        ////process jar or class file, generate class hash and find modified class
        def hashFile = new File(outputDir, HASH_TXT)
        hashFile.delete()
        hashFile.createNewFile()
        Set<File> inputFiles = dexTask.inputs.files.files
        inputFiles.each { inputFile ->
            def path = inputFile.absolutePath
            if (inputFile.isDirectory()) {
                NuwaProcessor.processClassPath(dirName, hashFile, inputFile, patchDir, hashMap, includePackage, excludePackage, excludeClass)
            } else if (path.endsWith(".jar")) {
                NuwaProcessor.processJar(hashFile, inputFile, patchDir, hashMap, includePackage, excludePackage, excludeClass)
            }

        }

        //dex and sign patch file
        if (patchDir && patchDir.exists()) {
            def patchFileName = "patch" + patchFilePrefixName + "-" + variant.name + ".apk"
            File patchFile = new File(patchDir, patchFileName)
            NuwaAndroidUtils.dex(project, patchDir, patchFile.absolutePath)
            NuwaAndroidUtils.signedApk(variant, patchFile)
        }

        //copy mapping.txt for next proguard
        def mapFile = new File("${project.buildDir}/outputs/mapping/${variant.dirName}/mapping.txt")
        def newMapFile = new File("${outputDir}/mapping.txt");
        if (mapFile.exists()) {
            FileUtils.copyFile(mapFile, newMapFile)
        }

    }
}


