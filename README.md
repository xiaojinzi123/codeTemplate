# codeTemplate

## 配置

1. 下载 codeTemplate.zip 解压到任何地方, 解压后的目录名字必须是：codeTemplate
2. 配置好环境变量, 任何地方能访问到 codeTemplate 这个命令

## 添加模板

占位符：
- 包路径：{ROOT_PACKAGE_NAME}
- 输入的名称：{NAME}
- 输入的名称 For Java: {JAVA_NAME}

在 codeTemplate 同级目录创建 codeTemplates 目录. 内部的每一个目录都是一个模板. 比如这里添加了一个 emo 的模板：

![image](https://user-images.githubusercontent.com/12975743/163428984-ba3b4a2e-f145-483d-9600-fc6a14e12113.png)

![image](https://user-images.githubusercontent.com/12975743/163429043-8fe7da07-47c4-41c8-b61c-53927c77bd01.png)

这里举例一个其中一个 ViewModel 类的范例：

```Kotlin
package {ROOT_PACKAGE_NAME}.{NAME}.view

import {ROOT_PACKAGE_NAME}.{NAME}.domain.{JAVA_NAME}UseCase
import {ROOT_PACKAGE_NAME}.{NAME}.domain.{JAVA_NAME}UseCaseImpl
import com.xiaojinzi.support.annotation.ViewLayer
import com.xiaojinzi.support.architecture.mvvm1.BaseViewModel

@ViewLayer
class {JAVA_NAME}ViewModel(
    private val useCase: {JAVA_NAME}UseCase = {JAVA_NAME}UseCaseImpl(),
): BaseViewModel(),
    {JAVA_NAME}UseCase by useCase{
}
```

占位符同样可使用在文件名称上：

![image](https://user-images.githubusercontent.com/12975743/163429720-8518633d-1b9c-4144-a7c8-1811a4323357.png)

## 使用命令生成代码

上述配置之后, 我们可以使用 codeTemplate 命令生成代码了. 
第一个参数为模板名称, 上述里面中我们叫 emo
第二个参数是名称. 比如登录：login

那么命令如下：
codeTemplate emo login  

意思就是使用了 emo 模板生成了代码. 名称是 login, 模板中的占位符会自动更换成对应的字符串. 

我自己使用的模板范例都会在这里：https://github.com/xiaojinzi123/codeTemplates


