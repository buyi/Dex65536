package com.buyi.util

import org.apache.commons.io.FileUtils
import org.apache.tools.ant.taskdefs.condition.Os
import org.apache.tools.ant.util.JavaEnvUtils
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

class JKAndroidUtils {

    private static final String PATCH_NAME = "patch.apk"

    public static dex(Project project, File classDir,String patchFileName) {
//        println ("classDir1" + classDir)
//        println ("classDir2" + classDir.absolutePath)
        println ("classDir3" + classDir.listFiles().size())
        if (classDir.listFiles().size()) {
            def sdkDir

            Properties properties = new Properties()
            File localProps = project.rootProject.file("local.properties")
            if (localProps.exists()) {
                properties.load(localProps.newDataInputStream())
                sdkDir = properties.getProperty("sdk.dir")
            } else {
                sdkDir = System.getenv("ANDROID_HOME")
            }

            if (sdkDir) {
                def cmdExt = Os.isFamily(Os.FAMILY_WINDOWS) ? '.bat' : ''
                def stdout = new ByteArrayOutputStream()
                println ("dex1" + "${sdkDir}/build-tools/${project.android.buildToolsVersion}/dx${cmdExt}")
                println ("dex2" + "${patchFileName}")
                println ("dex3" + "${classDir.absolutePath}")
                project.exec {
                    commandLine "${sdkDir}/build-tools/${project.android.buildToolsVersion}/dx${cmdExt}",
                            '--dex',
                            "--output=${patchFileName}",
                            "${classDir.absolutePath}"
                    standardOutput = stdout
                }

                project.exec {
                    commandLine "/bin/pwd"

                }
                project.exec {
                    commandLine "/usr/bin/java", "-jar", "../dex-method-counts.jar", "${patchFileName}"

                }

//                java -jar /Users/buyi/Downloads/dex-method-counts.jar /Users/buyi/Downloads/appnew/source/android/build/outputs/apk/android-jikexueyuan-armeabi-debug/assets/libs.dex
                def error = stdout.toString().trim()
                if (error) {
                    println "dex error:" + error
                }
            } else {
                throw new InvalidUserDataException('$ANDROID_HOME is not defined')
            }
        }
    }

    public static void cdAndls (Project project, String dir) {
        println ("cdAndls dir:${dir}")

        project.exec {
            commandLine "/usr/bin/cd", "${dir}"

        }

        project.exec {
            commandLine "/bin/ls", "-al"

        }

        project.exec {
            commandLine "/bin/pwd"

        }

//        project.exec {
//            commandLine "/usr/bin/cd", "-"
//
//        }
    }


    public static signedApk(def variant, File apkFile) {
        if(!apkFile.exists())
            return;

        def signingConfigs = variant.getSigningConfig()
        if (signingConfigs == null)
            return;

        def args = [JavaEnvUtils.getJdkExecutable('jarsigner'),
                    '-verbose',
                    '-sigalg', 'MD5withRSA',
                    '-digestalg', 'SHA1',
                    '-keystore', signingConfigs.storeFile,
                    '-keypass', signingConfigs.keyPassword,
                    '-storepass', signingConfigs.storePassword,
                    apkFile.absolutePath,
                    signingConfigs.keyAlias]

        println "Signing with command:"
        for (String s : args)
            print s + " "
        println ""
        def proc = args.execute()
        def outRedir = new StreamRedir(proc.inputStream, System.out)
        def errRedir = new StreamRedir(proc.errorStream, System.out)

        outRedir.start()
        errRedir.start()

        def result = proc.waitFor()
        outRedir.join()
        errRedir.join()

        if (result != 0) {
            throw new GradleException('Couldn\'t sign')
        }

    }

    static class StreamRedir extends Thread {
        private inStream
        private outStream

        public StreamRedir(inStream, outStream) {
            this.inStream = inStream
            this.outStream = outStream
        }

        public void run() {
            int b;
            while ((b = inStream.read()) != -1)
                outStream.write(b)
        }
    }


    public static zipalign(Project project, String apkFile) {
        if (apkFile.exists()) {
            println("Running zip align on final apk...")
            project.exec {
                executable ant.zipalign
                if (verbose) args '-v'
                args '-f', 4, apkFile, apkFile
            }
        }
    }

    public static applymapping(File proguardFile, File mappingFile) {
        if (mappingFile.exists()) {
            FileUtils.writeLines(proguardFile, ["-applymapping ${mappingFile.absolutePath}"])
        } else {
            println "$mappingFile does not exist"
        }
    }
}