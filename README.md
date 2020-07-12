# SZU_gwt_official_account_spider
深大公文通-公众号-每日文章爬虫

基于kotlin的深大公文通公众号爬虫

## 使用
### 运行方式
下载release中的文件，安装Java环境，运行命令
```bat
java -jar gwt_spider.jar
```
即可。

### 功能
随时随地都可以爬公文，可以是账号登陆，也可以是easyconnect连接。
可以自己规定爬的公文的时间、标题关键词、种类、上限，然后用py脚本能做个排序。

## 关于公众号
公众号每天发布的文章就来自这个程序，当然由于公众号发文全程是自动化的，所以是拿这个程序进行修改的。

其中sorter.py脚本是**早期**公众号发文的排序脚本，辅助人工判断进行排序。现在公众号发文的过程早已全部自动化，不需要人的参与，现在公文的排序是交由神经网络进行的，具体细节可以参考[https://github.com/chimaoshu/SZU_gwt_predict_network](https://github.com/chimaoshu/SZU_gwt_predict_network)

## 关于小程序

小程序的爬虫比这个复杂多了，涉及到图片、附件、截图、公文HTML预处理、数据库等等内容，所以用的不是这个程序。

![小程序](https://img-blog.csdnimg.cn/20200712180936240.png)