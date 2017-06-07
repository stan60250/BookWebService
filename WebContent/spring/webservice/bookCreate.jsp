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
<body class="is-faq">
    <header id="header">
        <p><font size="8" color="#666666" face="DFKai-sb">藏書閣</font></p>
        <p>電子書籍分享平台</p>
        <ul class="actions">
        	<li><font size="4"><a href="#" onclick="history.back()" class="button">返回書架</a></font></li><br><br>
        </ul>
    </header>
    <div id="main">
        <article id="modal" class="container">
            <header><h2>書籍上架</h2></header>
            <div class="content">
            	<form id="form" action="doBookCreate?token=$InfoModel.userToken" method="POST">
					<p>
						<font size="5" color="#888888">書籍名稱：</font>
						<input type="text" name="bookName" size="10" /><br>
						<font size="5" color="#888888">書籍摘要：</font>
						<input type="text" name="bookAbstract" size="10" /><br>
						<font size="5" color="#888888">書籍封面：</font>
						<input type="text" name="bookCover" size="10" /><br>
						<font size="5" color="#888888">訂閱點數：</font>
						<input type="text" name="bookCost" size="10" requiredaria-required="true" max ="20" min="1" /><br>
						<font size="5" color="#888888">書籍內容：</font>
						<textarea name="bookContent" cols="50" rows="5"></textarea>
						<input type="hidden" name="bookCreator" value="">
					</p>
					<p align="right">
						<input type="submit" value="上架" />
						<input type ="button" onclick="history.back()" value="返回" />
					</p>
				</form>
            </div>
        </article>
    </div>
    <footer></footer>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/js/skel.min.js"></script>
    <!--[if lte IE 8]><script src="/assets/js/ie/respond.min.js"></script><![endif]-->
    <script src="assets/js/main.js"></script>
</body>
</html>