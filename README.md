# Dex65536

用gradle groovy插件实现拆包，可以定制化打对应类以及jar包，使得目标包小于65535的限制。
在实际使用过程中还需要关注
1.不能接口 和 接口的实现被两个loader加载
2.不能父子类被两个loader加载

我说的所有情况出现在application加载时所需要的类。


实现步骤
1.建立buildSrc/src/main/groovy 目录 在这里写对应gragle plugin插件逻辑 这里使用了Transform API
2.在对应build.gradle 使用apply plugin 对应包名来引入对应插件
3.在transform中写对应打包逻辑 transform类似于打包的责任链模式 上一个transform的输出是下一个transform的输入。
我们通过干预其中的过程，来打出我们想要的apk zip或者dex包
4.需要在Application中启用