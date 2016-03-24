

import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.android.build.api.transform.QualifiedContent.ContentType
import com.android.build.api.transform.QualifiedContent.Scope
import org.gradle.api.Project
import com.android.build.api.transform.Context
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.gradle.internal.pipeline.TransformManager;
import org.apache.commons.codec.digest.DigestUtils
import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import org.apache.commons.io.FileUtils
import com.buyi.util.JKAndroidUtils
class TransformImpl extends Transform {
    Project project
    public TransformImpl(Project project) {
        println("transform:TransformImpl")
        this.project = project
    }

    @Override
    String getName() {
        println("transform:getName")
        return "TransformImpl"
    }

    @Override
    Set<ContentType> getInputTypes() {
        println("transform:getInputTypes")
        return TransformManager.CONTENT_CLASS;
    }


    @Override
    Set<Scope> getScopes() {
        println("transform:getScopes")
        return TransformManager.SCOPE_FULL_PROJECT;
    }


    @Override
    boolean isIncremental() {
        println("transform:isIncremental")
        return false;
    }

    @Override
    void transform(
            @NonNull Context context,
            @NonNull Collection<TransformInput> inputs,
            @NonNull Collection<TransformInput> referencedInputs,
            @Nullable TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        println("transform inputs!!" + inputs)
        println("transform referencedInputs!!" + referencedInputs)
        println("transform outputProvider!!" + outputProvider)
        File jarDest = new File("assets/alljar");
        jarDest.mkdirs();
        inputs.each { TransformInput input ->

//            input.directoryInputs.each { DirectoryInput directoryInput ->
//                println("directoryInput.name:" + directoryInput.name)
//                println("directoryInput.scopes:" + directoryInput.scopes)
//                println("directoryInput.contentTypes:" + directoryInput.contentTypes)
//                println("directoryInput.file:" + directoryInput.file)
//                println("directoryInput.properties:" + directoryInput.properties)
//
//            }
//
//
//            input.jarInputs.each { JarInput jarInput ->
//                println("jarInput.name:" + jarInput.name)
//                println("jarInput.scopes:" + jarInput.scopes)
//                println("jarInput.contentTypes:" + jarInput.contentTypes)
//                println("jarInput.file:" + jarInput.file)
//                println("jarInput.properties:" + jarInput.properties)
//
//            }
            /**
             * 遍历目录
             */

            input.directoryInputs.each { DirectoryInput directoryInput ->
                /**
                 * 获得产物的目录
                 */
                println("directoryInput.name:" + directoryInput.name)
                println("directoryInput.contentTypes:" + directoryInput.contentTypes)
                println("directoryInput.scopes:" + directoryInput.scopes)





                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
                String buildTypes = directoryInput.file.name
                String productFlavors = directoryInput.file.parentFile.name
                //这里进行我们的处理 TODO

                print("dest1 is ${dest.absolutePath}")
                print("dest2 is ${directoryInput.name}")
                print("dest3 is ${buildTypes}")
                print("dest4 is ${productFlavors}")

                project.logger.error "Copying ${directoryInput.name} to ${dest.absolutePath}"
                /**
                 * 处理完后拷到目标文件
                 */
                FileUtils.copyDirectory(directoryInput.file, dest);
            }

            Set <QualifiedContent.ContentType> types = new HashSet<QualifiedContent.ContentType>();
            types.add (QualifiedContent.DefaultContentType.CLASSES);

            Set<QualifiedContent.Scope> scopes = new HashSet<QualifiedContent.Scope> ();
            scopes.add (QualifiedContent.Scope.PROJECT_LOCAL_DEPS);
            List< QualifiedContent> contents = new ArrayList<QualifiedContent>();

//            outputProvider.getContentLocation("alljar", types, scopes, Format.DIRECTORY);
            /**
             * 遍历jar
             */
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name;
                /**
                 * 重名名输出文件,因为可能同名,会覆盖
                 */
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath);
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4);
                }
                /**
                 * 获得输出文件
                 */


                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);

                //处理jar进行字节码注入处理TODO



//                project.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"

//                println("jar1 is ${dest.absolutePath}")
//                println("jar2 is ${jarInput.file.absolutePath}")
//                println("jar3 is ${destName}")


//                jar1 is /Users/buyi/Downloads/appnew/source/android/build/intermediates/transforms/TransformImpl/jikexueyuan/debug/jars/1/4/classes_ac92f46bac0e0d496523c2383703786f.jar
//                jar2 is /Users/buyi/Downloads/appnew/source/android/build/intermediates/exploded-aar/android/platform/unspecified/jars/classes.jar
//                jar3 is classes

//                println ("contents1 is " + ((QualifiedContent)jarInput).scopes)
                if (((QualifiedContent)jarInput).scopes.contains(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)) {
                    contents.add (jarInput);
                    if (jarInput.file.absolutePath.contains("jpush")) {
                        FileUtils.copyFile(jarInput.file, dest);
                        println ("Copying22 ${jarInput.file.absolutePath} to ${dest.absolutePath}")
                    } else {
                        FileUtils.copyFileToDirectory(jarInput.file,jarDest)
                        println ("Copying22 ${jarInput.file.absolutePath} to ${jarDest.absolutePath}")
                    }

                } else {
                    FileUtils.copyFile(jarInput.file, dest);
                    println ("Copying22 ${jarInput.file.absolutePath} to ${dest.absolutePath}")
                }
                println ("contents2 is " + contents)



//                JKAndroidUtils.signedApk()


            }

        }
        println ("dex before~~")
        File file = new File ("assets/libs.apk");
//            file.mkdirs();
        JKAndroidUtils.dex(project, jarDest, file.absolutePath);
        for (File file1 : jarDest.listFiles()) {
            file1.delete();
        }
        jarDest.delete();

        println ("dex after~~")
    }
//    @Override
//    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        println("transform inputs!!" + inputs)
//        println("transform referencedInputs!!" + referencedInputs)
//        println("transform outputProvider!!" + outputProvider)
///**
// * 遍历输入文件
// */
//        inputs.each { TransformInput input ->
//            /**
//             * 遍历目录
//             */
//            input.directoryInputs.each { DirectoryInput directoryInput ->
//                /**
//                 * 获得产物的目录
//                 */
//                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY);
//                String buildTypes = directoryInput.file.name
//                String productFlavors = directoryInput.file.parentFile.name
//                //这里进行我们的处理 TODO
//                project.logger.error "Copying ${directoryInput.name} to ${dest.absolutePath}"
//                /**
//                 * 处理完后拷到目标文件
//                 */
//                FileUtils.copyDirectory(directoryInput.file, dest);
//            }
//
//            /**
//             * 遍历jar
//             */
//            input.jarInputs.each { JarInput jarInput ->
//                String destName = jarInput.name;
//                /**
//                 * 重名名输出文件,因为可能同名,会覆盖
//                 */
//                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath);
//                if (destName.endsWith(".jar")) {
//                    destName = destName.substring(0, destName.length() - 4);
//                }
//                /**
//                 * 获得输出文件
//                 */
//                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR);
//
//                //处理jar进行字节码注入处理TODO
//
//                FileUtils.copyFile(jarInput.file, dest);
//                project.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"
//            }
//        }
//
//
//    }

}