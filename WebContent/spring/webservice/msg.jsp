<!DOCTYPE HTML>
<html>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<head>
    <title>藏書閣 - 訊息</title>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<meta name="apple-mobile-web-app-title" content="藏書閣" />
	<meta http-equiv="refresh" content="$MessageModel.sec;url=$MessageModel.url">
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
        	<!--LIST MENU START-->
			<li><font size="4"><a href="doListPublic" class="button">公共書架</a></font></li><br><br>
			<!--LIST MENU END-->
        </ul>
    </header>
    <div id="main">
        <article id="modal" class="container">
            <header><h2>訊息</h2></header>
            <div class="content">
				<p align="center">
					<font size="5" color="#888888">
						$MessageModel.message
					</font>
				</p>
				<br>
				<p align="right">
					<input type ="button" onclick="window.location='$MessageModel.url'" value="立即前往" />
				</p>
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