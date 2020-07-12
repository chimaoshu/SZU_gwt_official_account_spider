import org.jsoup.Connection
import org.jsoup.Jsoup
import java.lang.NumberFormatException

fun theNetworkIsOK (cookies:Map<String, String>?) : Boolean {

    val respond =

    if (cookies == null){
        println("正在检测VPN连接状况....")
        //直接访问列表，若无VPN则会跳转到登录页面，登录页面有元素"username"

        Jsoup.connect("https://www1.szu.edu.cn/board/infolist.asp")
        .method(Connection.Method.GET)
        .execute()
        .parse()
        .getElementById("username")
    }

    else{
        println("正在检测cookies有效性....")
        //填入cookies访问列表，若无登陆则会跳转到登录页面，登录页面有元素"username"

        Jsoup.connect("https://www1.szu.edu.cn/board/infolist.asp")
        .method(Connection.Method.GET)
        .cookies(cookies)
        .execute()
        .parse()
        .getElementById("username")
    }

    //出现null意味着页面没有跳转，也就意味着网络没问题
    return respond == null
}

fun restartVPN(){
    Runtime.getRuntime().exec("cmd.exe /c start C:\\inetpub\\wwwroot\\重启VPN.bat")
}

fun main(args: Array<String>)  {

    //区别是VPN模式还是非VPN登陆模式
    //var networkMode = "VPN"其实不用,但看cookies是不是null就完事了

    var cookies : Map<String,String>? = null

    if (!theNetworkIsOK(cookies)){

        var userInput:String
        loop@ while (true){

            println("""
            ==================================
            |检测到VPN断开，你有以下选择：
            |1.以无VPN模式登录
            |2.启动VPN
            |3.我已自行启动VPN
            |4.爷不爬了
            ==================================
            请输入你的选择:
            """.trimIndent())
            userInput = readLine().toString()
            when(userInput){
                "1" -> {

                    //跳转到负责登录的函数
                    cookies = network_prepare.login()

                    //如果网络正常
                    if (theNetworkIsOK(cookies))
                        break@loop
                    else
                        continue@loop
                }
                "2" -> {restartVPN() ; if (theNetworkIsOK(cookies)) break@loop else continue@loop} //跳转到重启VPN的函数
                "3" -> {if (theNetworkIsOK(cookies)) break@loop else continue@loop}
                "4" -> {return}
                "" -> {

                    //跳转到负责登录的函数
                    cookies = network_prepare.login()

                    //如果网络正常
                    if (theNetworkIsOK(cookies))
                        break@loop
                    else
                        continue@loop
                }
                else -> {println("输入错误，请重新输入") ; continue@loop}
            }
        }
    }
    println(
        """
    ==============================
    |网络准备就绪，可以开始爬内容了  
    |进入爬虫设置...               
    |你可以进行爬虫设置             
    ==============================
    """.trimIndent())

    var timeSetting = "今天"
    var typeSetting = "所有"
    var keywordSetting:String? = ""
    var limitationSetting = 0
    var userInput:String

    loop@ while (true){
        print(
            """
        ===============================================
        |现在的设置是：
        |时间设置：${timeSetting}
        |种类设置：${typeSetting}
        |关键词设置：${keywordSetting}
        |上限设置：${limitationSetting}（0表示无上限）
        |==============================================
        |你可以：
        |1.修改时间设置（默认：今天）
        |2.修改种类设置（默认：所有）
        |3.修改公文标题关键词设置（默认：无关键词）
        |4.修改公文爬取数量上限设置（默认：无上限）
        |5.给爷爬!
        ===============================================
        请输入你的选择：
         """.trimIndent())
        userInput = readLine().toString()
        when(userInput){
            "1" -> {
                println(
                    """
                ========================================================
                |修改时间设置为：
                |1.今天    
                |2.一周内
                |3.30天内
                |4.2020年（总有一天这个选项会失效，等学校修复漏洞就用不了了）
                |5.老子不改了，返回上一步
                ========================================================
                请输入你的选择：
                """.trimIndent())

                userInput = readLine().toString()
                timeSetting = when (userInput) {
                    "1" -> "今天"
                    "2" -> "一周内"
                    "3" -> "30天内"
                    "4" -> "2020年"
                    else -> continue@loop
                }
            }
            "2" -> {
                println(
                    """
                    ================================
                    |修改种类设置为：
                    |1.所有（默认）
                    |2.讲座
                    |3.教务
                    |4.科研
                    |5.行政
                    |6.学工
                    |7.排行
                    |8.老子不改了,返回上一步
                    ================================
                    请输入你的选择：
                    """.trimIndent())

                userInput = readLine().toString()
                typeSetting = when (userInput) {
                    "1" -> "所有"
                    "2" -> "讲座"
                    "3" -> "教务"
                    "4" -> "科研"
                    "5" -> "行政"
                    "6" -> "学工"
                    "7" -> "排行"
                    else -> continue@loop
                }
            }
            "3" -> {
                println("""
                    ================================
                    |要将关键字修改为:
                    |(若直接按enter键,则为无关键字)
                    ================================
                    请输入你的修改:
                """.trimIndent())
                keywordSetting = readLine().toString()
            }
            "4" -> {
                println("""
                    ==============================
                    |要将上限修改为:
                    |(若输入0,则为无关键字)
                    ==============================
                    请输入你的修改:
                """.trimIndent())
                userInput = readLine().toString()
                try {
                    limitationSetting = userInput.toInt()

                    if (limitationSetting < 0) {
                        limitationSetting = (limitationSetting * (-1))
                    }

                    if (limitationSetting > 1000) {
                        limitationSetting = 1000
                    }
                }
                catch (e : NumberFormatException) {
                    println("输入错误,返回上一步\n")
                    continue@loop
                }
            }
            "5" -> break@loop
            "" -> break@loop
        }
    }

    val allSetting = mapOf(
        "timeSetting" to timeSetting,
        "typeSetting" to typeSetting,
        "keywordSetting" to keywordSetting,
        "limitationSetting" to limitationSetting,
        "cookies" to cookies,
        "modeSetting" to if (cookies == null) "VPN" else "login"
                          )
    get_document.get_document_list.getDocumentsList(allSetting)
}
