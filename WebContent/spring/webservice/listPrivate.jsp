<!DOCTYPE HTML>
<html>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<head>
    <title>藏書閣 - 電子書籍分享平台</title>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <meta name="apple-mobile-web-app-title" content="藏書閣" />
    <!--[if lte IE 8]><script src="/assets/js/ie/html5shiv.js"></script><![endif]-->
    <link rel="stylesheet" href="assets/css/main.css" />
    <link rel="icon" href="assets/icons/favicon.ico" type="image/x-icon" />
    <link rel="apple-touch-icon-precomposed" href="assets/icons/apple-touch-icon-precomposed.png" />
    <!--[if lte IE 8]><link rel="stylesheet" href="/assets/css/ie8.css" /><![endif]-->
</head>
<body class="is-landing">
    <header id="header">
        <p><font size="8" color="#666666" face="DFKai-sb">藏書閣</font></p>
        <p>電子書籍分享平台</p>
        <ul class="actions">
			<!--LIST MENU START-->
			#foreach( $menuModel in $BookListModel.menuListModel )
				<li><font size="4"><a href="$menuModel.url" class="button">$menuModel.text</a></font></li><br><br>
			#end
			<!--LIST MENU END-->
        </ul>
    </header>
    <div id="main">
        <div class="items">
			<!--LIST ITEM START-->
			#foreach( $bookModel in $BookListModel.bookListModel )
				<article class="item">
					<header>
						<h3><a href="bookView?token=$BookListModel.token&id=$bookModel.id">$bookModel.bookName</a></h3><span class="type">$bookModel.bookCreator</span></header>
					<a href="bookView?token=$BookListModel.token&id=$bookModel.id" class="image"><img src="$bookModel.bookCover" alt="" /></a>
					<footer>
						<div class="inner">
							<p>$bookModel.bookAbstract</p><a href="bookView?token=$BookListModel.token&id=$bookModel.id" class="button">閱讀<span class="extra"></span></a>
						</div>
					</footer>
				</article>
			#end
			<!--LIST ITEM END-->
        </div>
		<!-- 頁碼 -->
        <!--<ul class="pagination">
            <li><span class="button special disabled">Previous</span></li>
            <li class="page current"><a href="index.html">1</a></li>
            <li class="page"><a href="2.html">2</a></li>
            <li class="page"><a href="3.html">3</a></li>
            <li class="page"><a href="4.html">4</a></li>
            <li><a href="2.html" class="button special">Next</a></li>
        </ul>-->
		<!-- 置頂廣告 -->
        <div id="ad1">
            <div class="unit">
                <!--<div id="bsap_1271184" class="bsarocks bsap_f31c75c39f902f049c7e4b2d8bbe2d77"></div>-->
            </div>
        </div>
		<!-- 置底廣告 -->
        <!--<div id="ad3">
            <div class="unit">
                <div id="bsap_1303590" class="bsarocks bsap_f31c75c39f902f049c7e4b2d8bbe2d77"></div>
            </div>
        </div>-->
    </div>
    <footer></footer>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/js/skel.min.js"></script>
    <!--[if lte IE 8]><script src="/assets/js/ie/respond.min.js"></script><![endif]-->
    <script src="assets/js/main.js"></script>
</body>
</html>