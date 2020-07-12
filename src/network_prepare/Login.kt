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

//fun main() {
//    val cookies = login()
//    logout(cookies!!)
//}

///**
// * 用于连接指定的网址 返回parse后的网页
// */
//@Throws(CookieResetException::class)
//fun connect(url: String, cookies: Map<String, String>) : Document {
//
//    // jsoup创建连接
//    val doc = Jsoup.connect(url)
//                   .cookies(cookies)
//                   .ignoreContentType(true) // 忽略类型验证
//                   .followRedirects(true) // 允许重定向
//                   .get()
//
//    // 找不到登录页面表单 则说明是正常情况 返回网页文档
//    doc.getElementById("casLoginForm") ?: return doc
//
//    // 如果找到了首页登录表单 说明cookie失效 被重定向到登录页面
//    throw CookieResetException("cookie has been reset")
//}
//
///**
// * 获取指定网址的公文列表
// */
//fun getPaperworks(boardUrl: String , cookies : Map<String, String>?) : List<Paperwork> {
//
//    val paperworkList = mutableListOf<Paperwork>()
//
//    if (cookies != null) {
//        // 连接网页
//        val doc = Jsoup.connect(boardUrl)
//            .method(Connection.Method.GET)
//            .cookies(cookies)
//            .execute()
//            .parse()
//    }
//    val tbody = doc.body()
//                   .getElementsByAttributeValue("style", "border-collapse: collapse")
//                   .first()
//                   .child(0)
//
//    // 访问公文列表 去除前两个无用元素
//    for (paperworkTd in tbody.children().drop(2)) {
//
//        val aList = paperworkTd.getElementsByTag("a")
//        val tdList = paperworkTd.getElementsByTag("td")
//
//        val title = aList[2].text()
//        val type = aList[0].text()
//        val unit = aList[1].text()
//        val date = tdList[5].text()
//        val clickTimes = tdList[6].text()
//        val url = "https://www1.szu.edu.cn/board/" + aList[2].attr("href")
//
//        paperworkList.add(Paperwork(title, type, unit, date, clickTimes, url))
//    }
//    return paperworkList
//}
//
///**
// * 获取全部公文
// */
//fun refreshAllPaperwork() {
//
//    val paperworkMap = mutableMapOf<String, List<Paperwork>>()
//
//    baseUrlMap.forEach { type, url ->
//        try {
//            paperworkMap[type] = getPaperworks(url)
//        }
//        // cookie失效 重新登录
//        catch (_: CookieResetException) {
//            login()
//            paperworkMap[type] = getPaperworks(url)
//        }
//    }
//    allPaperworkMap = paperworkMap
//}
//
//fun getPaperworkHtml(paperwork: Paperwork) : String {
//    val doc = Jsoup.connect(paperwork.url)
//                   .cookies(cookies)
//                   .get()
//    val html = doc.getElementsByAttributeValue("style", "border-collapse: collapse;word-break:break-all;")[0]
//    html.removeAttr("width")
//    return html.outerHtml()
//}
//
//fun getPaperworkHtmlById(id: String) : String {
//    val doc = Jsoup.connect("https://www1.szu.edu.cn/board/view.asp?id=$id")
//            .cookies(cookies)
//            .get()
//    /*
//    val html = doc.getElementsByTag("td")
//            .findLast { it.attr("valign") == "top" && it.attr("height") == "400" }
//            ?: return "ERROR"*/
//    val html = doc.getElementsByAttributeValue("style", "border-collapse: collapse;word-break:break-all;")[0]
//    html.removeAttr("width")
//    return html.outerHtml()
//}
//
//val baseUrlMap = mapOf(
//        "sb" to "https://www1.szu.edu.cn/board/infolist.asp"
//        /*"排行" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%B5%E3%BB%F7",
//        "生活" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%C9%FA%BB%EE",
//        "讲座" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%BD%B2%D7%F9",
//        "学工" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%D1%A7%B9%A4",
//        "教务" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%BD%CC%CE%F1",
//        "科研" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%BF%C6%D1%D0",
//        "行政" to "https://www1.szu.edu.cn/board/infolist.asp?infotype=%D0%D0%D5%FE"
//         */
//)
//
//var allPaperworkMap : Map<String, List<Paperwork>> = mapOf()
//    private set(value) { field = value }
//
//
//
//
//fun main() {
//
//    login()
//    refreshAllPaperwork()
//    /*
//    listOf(
//            403644, 403791, 407668, 407738, 407931, 408210, 408255, 408280, 408284, 408298, 408314, 408346, 408373, 408374, 408430,
//    408434, 408442, 408467, 408475, 408492, 408501, 408511, 408524, 408528, 408534, 408540, 408541, 408554, 408558, 408583, 408591,
//    408603, 408605, 408609, 408622, 408623, 408624, 408639, 408651, 408666, 408682, 408683, 408684, 408704, 408718, 408723, 408727,
//    408734, 408759, 410378, 410918, 411441, )*/
//            listOf(410916)
//            .map { it.toString() }
//            .map { Pair(it, getPaperworkHtmlById(it)) }
//            //.forEach { FileUtil.outputSingle("C:/Users/14193/Desktop/公文/${it.first}.html", it.second) }
//    logout()
//}
//
//data class Paperwork(val title: String, val type: String, val unit: String, val date: String, val clickTimes: String, val url: String)
//
//class CookieResetException(message: String) : RuntimeException(message)