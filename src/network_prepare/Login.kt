package network_prepare

import org.jsoup.Connection
import org.jsoup.Jsoup
import theNetworkIsOK
import javax.script.Invocable
import javax.script.ScriptEngineManager
import java.io.File
//import java.lang.RuntimeException
//import org.jsoup.nodes.Document

//登陆,获得cookies
fun login(): Map<String, String>? {

    var cookies: Map<String, String>
    var username = ""
    var password = ""
    var userInput: String

    while (true) {

        loop@ while (true) {


            //${username} ${password}
            println(
                """
            =========================
            |        登录模式        |
            =========================
            |学号:${username}
            |密码:${password}
            |你有以下选择:
            |1.修改学号
            |2.修改密码
            |3.登陆
            |4.退出登录模式
            =========================
            请输入你的选择:
        """.trimIndent()
                   )

            userInput = readLine().toString()
            when (userInput) {
                "1" -> {
                    println("请输入你的学号:")
                    username = readLine().toString()
                }
                "2" -> {
                    println("请输入你的密码:")
                    password = readLine().toString()
                }
                "3" -> break@loop
                "4" -> return null
                "" -> break@loop
                else -> continue@loop
            }
        }

        println("请求登录页面...")
        // 第一次连接 获取表单隐藏数据
        println("请求登录页面...")
        val firstConnection =
            Jsoup.connect("https://authserver.szu.edu.cn/authserver/login?service=http%3A%2F%2Fwww1%2Eszu%2Eedu%2Ecn%2Fmanage%2Fcaslogin%2Easp%3Frurl%3D%2F")
                .method(Connection.Method.GET)
                .execute()


        // 获取第一次连接的登录表单
        println("获取登录表单...")
        val form = firstConnection.parse()
            .getElementById("casLoginForm")


        //获得AES加密后的密码
        val pwdDefaultEncryptSalt = form.getElementById("pwdDefaultEncryptSalt").attr("value")
        val jsCode = File("encrypt.js").readText()
        val engine = ScriptEngineManager().getEngineByName("javascript")
        engine.eval(jsCode)
        val invocable = engine as Invocable
        val encryptedData = invocable.invokeFunction("encryptAES", password, pwdDefaultEncryptSalt)


        // 构造表单参数
        println("构建登录参数...")
        val formData = mapOf(
            "username" to username,
            "password" to encryptedData.toString(),
            "rememberMe" to "on",
            "lt" to form.getElementsByAttributeValue("name", "lt").attr("value"),
            "dllt" to form.getElementsByAttributeValue("name", "dllt").attr("value"),
            "execution" to form.getElementsByAttributeValue("name", "execution").attr("value"),
            "_eventId" to form.getElementsByAttributeValue("name", "_eventId").attr("value"),
            "rmShown" to form.getElementsByAttributeValue("name", "rmShown").attr("value")
                            )

        println("构建请求头...")
        // 构造请求头
        val headers = mapOf(
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
            "Accept-Encoding" to "gzip, deflate, br",
            "Accept-Language" to "zh-CN,zh;q=0.9,en;q=0.8",
            "Cache-Control" to "max-age=0",
            "Connection" to "keep-alive",
            //"Content-Length"  to "175",
            "Content-Type" to "application/x-www-form-urlencoded",
            //"Host"            to "authserver.szu.edu.cn",
            //"Origin"          to "https://authserver.szu.edu.cn",
            //"Referer"         to "https://authserver.szu.edu.cn/authserver/login?service=http%3A%2F%2Fwww1%2Eszu%2Eedu%2Ecn%2Fmanage%2Fcaslogin%2Easp%3Frurl%3D%2F",
            "Sec-Fetch-Mode" to "navigate",
            "Sec-Fetch-Site" to "same-origin",
            "Sec-Fetch-User" to "?1",
            "Upgrade-Insecure-Requests" to "1",
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36 Edg/79.0.309.65"
                           )

        println("发送登录请求...")
        // 将数据POST指定网址登录
        val login =
            Jsoup.connect("https://authserver.szu.edu.cn/authserver/login?service=http%3A%2F%2Fwww1%2Eszu%2Eedu%2Ecn%2Fmanage%2Fcaslogin%2Easp%3Frurl%3D%2F")
                .ignoreContentType(true) // 忽略类型验证
                .followRedirects(true) // 禁止重定向
                .postDataCharset("utf-8")
                .cookies(firstConnection.cookies()) // 将第一次访问的cookie用于POST
                .headers(headers)
                .data(formData)
                .method(Connection.Method.POST)
                .execute()

        cookies = login.cookies()

        if (theNetworkIsOK(cookies)) {
            println("登陆成功!")
            return cookies
        }

        else {
            println("登陆失败,请检查学号与密码")
        }
    }
}

////注销
//fun logout(cookies:Map<String,String>) {
//    Jsoup.connect("https://www1.szu.edu.cn/manage/caslogout.asp")
//            .cookies(cookies)
//            .execute()
//}
