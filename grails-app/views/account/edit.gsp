<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>My Account</title>

</head>

<body>

<h1>My Account</h1>
<tmpl:/templates/message/>

<!-- Nav tabs -->
<ul class="nav nav-tabs" role="tablist">
    <li class="active"><a href="#account" role="tab" data-toggle="tab">Profile</a></li>
    <li><a href="#blogs" role="tab" data-toggle="tab">Blogs</a></li>
    <li><a href="#newblog" role="tab" data-toggle="tab">New blog</a></li>
</ul>

<!-- Tab panes -->

<div class="tab-content">
    <div class="tab-pane active well well-lg" id="account">
        <tmpl:account/>
    </div>

    <div class="tab-pane well well-lg" id="blogs">
        <tmpl:blogs/>
    </div>

    <div class="tab-pane well well-lg" id="newblog">
       <tmpl:addBlog/>
    </div>
</div>


<script language="javascript">

    function showSpinner() {
        document.all.spinner.style.display = "inline";
        // new Effect.Fade('feedResults');
    }

    function hideSpinner() {
        document.all.spinner.style.display = "none"
    }

    function appearDiv() {
        new Effect.Appear('feedResults');
    }


</script>

</body>
</html>
