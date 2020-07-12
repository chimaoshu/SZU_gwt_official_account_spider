package get_document.get_document_list

import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.File
import java.net.URLEncoder

fun getDocumentsList(allSetting:Map<String,Any?>) {

    val formData = mapOf(
        "dayy" to
                when (allSetting["timeSetting"]) {
                    "今天" -> "1#今天" //"1%23%BD%F1%CC%EC"
                    "一周内" -> "7#一周内"
                    "30天内" -> "30#30天内"
                    "2020年" -> "2011"
                    else -> "1#今天"
                },
        "from_username" to "",
        "keyword" to "",
        "searchb1" to "搜索"
                        )

    val headers = mapOf(
//        ":authority" to "www1.szu.edu.cn",
//        ":method" to "POST",
//        ":path" to "/board/infolist.asp?",
//        ":scheme" to "https",
        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
        "Accept-Encoding" to "gzip, deflate, br",
        "Accept-Language" to "zh-CN,zh;q=0.9,en;q=0.8",
        "Cache-Control" to "max-age=0",
        "Connection" to "keep-alive",
        //"Content-Length" to "73",
        "Content-Type" to "application/x-www-form-urlencoded",
        "DNT" to "1",
        "Host" to "www1.szu.edu.cn",
        "Origin" to "https://www1.szu.edu.cn",
        "Referer" to "https://www1.szu.edu.cn/board/infolist.asp",
        "Sec-Fetch-Dest" to "document",
        "Sec-Fetch-Mode" to "navigate",
        "Sec-Fetch-Site" to "same-origin",
        "Sec-Fetch-User" to "?1",
        "Upgrade-Insecure-Requests" to "1",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36"
                       )

    @Suppress("UNCHECKED_CAST")
    val respond =
        if (allSetting["modeSetting"] == "login") {
            Jsoup.connect("https://www1.szu.edu.cn/board/infolist.asp")
                .headers(headers)
                .cookies(allSetting["cookies"] as MutableMap<String, String>?)
                .data(formData)
                .method(Connection.Method.POST)
                .postDataCharset("gbk")
                .execute()
                .parse()
        }
        else {
            Jsoup.connect("https://www1.szu.edu.cn/board/infolist.asp")
                .headers(headers)
                .data(formData)
                .method(Connection.Method.POST)
                .postDataCharset("gbk")
                .execute()
                .parse()
        }

    val tbody = respond.body()
        .getElementsByAttributeValue("style", "border-collapse: collapse")
        .first()
        .child(0)

    // 访问公文列表 去除前两个无用元素
    val limitationSetting :Int = allSetting["limitationSetting"] as Int
    var documentNumber = 0
    var text = ""
    val typeSetting = allSetting["typeSetting"]

    for (everyLine in tbody.children().drop(2)) {

        if ((documentNumber > limitationSetting) and (limitationSetting != 0))
            break

        val aList = everyLine.getElementsByTag("a")
        val tdList = everyLine.getElementsByTag("td")

        val type = aList[0].text()
        if ((typeSetting == "所有") or (typeSetting == type)) {

            documentNumber ++
            val title = aList[2].text()
            val department = aList[1].text()
            val date = tdList[5].text()
            //val clickTimes = tdList[6].text()
            val url = "pages/document/document.html?id=" + aList[2].attr("href").split("=")[1] + "&title=" + title
            text += "1$${title}$${department}$${date}$${url}\n"
        }
        else
            continue
    }

    println("爬取完成,已将数据储存至本地,正在调用模板生成器...")
    val file = File("todayList.txt")
    file.writeText(text)

    Runtime.getRuntime().exec("python template_generator.py")
    println("已完成")
}
//
//fun main() {
//    val allSetting = mapOf(
//        "timeSetting" to "今天",
//        "typeSetting" to "所有",
//        "keywordSetting" to "",
//        "limitationSetting" to 0,
//        "cookies" to null,
//        "modeSetting" to "VPN"
//                          )
//    getDocumentsList(allSetting)
//
//}