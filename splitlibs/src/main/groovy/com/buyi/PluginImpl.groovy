package com.buyi
import org.gradle.api.Project
import org.gradle.api.Plugin
import com.android.build.gradle.AppExtension

public class PluginImpl implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        /**
         * 注册transform接口
         */
//        def isApp = project.plugins.hasPlugin(AppPlugin)
//        if (isApp) {
        println("extensions:" + project.extensions)
            def android = project.extensions.getByType(AppExtension)
            def transform = new TransformImpl(project)
        println("transform:" + transform)
            android.registerTransform(transform)

//        }


        project.afterEvaluate {

            project.android.applicationVariants.each { variant ->
                println("variant:" + variant.name)
                println("variant:" + variant.getDirName())
            def proguardTask = project.tasks.findByName("transformClassesAndResourcesWithProguardFor${variant.name.capitalize()}")
            println "proguardTask is ${proguardTask}";
            if (proguardTask) {
                project.logger.error "proguard=>${variant.name.capitalize()}"

                proguardTask.inputs.files.files.each { File file->
                    project.logger.error "file inputs=>${file.absolutePath}"
                }

                proguardTask.outputs.files.files.each { File file->
                    project.logger.error "file outputs=>${file.absolutePath}"
                }
            }

            def dexTask1 = project.tasks.findByName("transformClassesWithDexFor${variant.name.capitalize()}")
            println "dexTask is ${dexTask1}";


            if (dexTask1) {
                project.logger.error "dex=>${variant.name.capitalize()}"
//
//                    println "dexTask.inputs is ${dexTask1.inputs}";
//                    println "dexTask.inputs.files is ${dexTask1.inputs.files}";
//                    println "dexTask.inputs.files.files is ${dexTask1.inputs.files.files}";
                dexTask1.inputs.files.files.each { File file->
//                        println "file is ${file}";
                    project.logger.error "file inputs=>${file.absolutePath}"
                }

                dexTask1.outputs.files.files.each { File file->
                    project.logger.error "file outputs=>${file.absolutePath}"
                }
            }

            def testTask = project.tasks.findByName("transformClassesWithTransformImplFor${variant.name.capitalize()}")
            println "testTask is ${testTask}";
            if (testTask) {

                Set<File> testTaskInputFiles = testTask.inputs.files.files
                Set<File> testTaskOutputFiles = testTask.outputs.files.files
                Set<File> exportFiles = new HashSet<File>();
                project.logger.error "Name:transformClassesWithTransformImpl=====>${testTask.name} input"
                testTaskInputFiles.each { inputFile ->
                    def path = inputFile.absolutePath
                    project.logger.error path
                    if (path.contains('libs')) {
                        exportFiles.add (inputFile);
                    }
                }

                project.logger.error "ready to apk=====>before"
                exportFiles.each { aExportFile ->
                    def path = aExportFile.absolutePath
                    project.logger.error path
                }

//                    def patchFileName = "patch" + "-" + variant.name + ".apk"
//                    File patchFile = new File("/Users/buyi/Downloads/appnew/source/android/libs/patch", patchFileName)
//                    NuwaAndroidUtils.dex(project, "/Users/buyi/Downloads/appnew/source/android/libs", patchFile.absolutePath)
//                    NuwaAndroidUtils.signedApk(variant, patchFile)
                project.logger.error "ready to apk=====>after"

                project.logger.error "Name:transformClassesWithTransformImpl=====>${testTask.name} output"
                testTaskOutputFiles.each { inputFile ->
                    def path = inputFile.absolutePath
                    project.logger.error path
                }
            }


        }}
    }
}