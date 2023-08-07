# Intro

a fast and dirty java web framework

![](images/kate.jpg)

# Prerequisites

Java19+

# Quick Start

```java

RouteRegister rr = new RouteRegister() {
    @Override
    public void apply(Router router) {
        router.get("/").handler(ctx -> ctx.response().end("DONE!"));
        router.get("/page").handler(ctx -> {
        ResponseUtils.html(ctx, "__templateContent__", 200);
    });
}

};

Kate kate = new Kate(new RouteRegister[]{
    rr
});
kate.start("localhost", 9999);
System.in.read();
kate.stop();
```

If you would like to use spring/springboot, annotate your `RouteRegister`(s) with `@Component/@Service/@Repository...` annotations to enable auto-scan and auto-wire to ease your DX(Developer Experiences).

> NOTE
> 
> The order of `RouteRegister`(s) matters!
> That's why array(or list) is as bootstrap parameters' holder.

# TIPS

## Fancy transition to make MPA a SPA

Although Kate is mainly for MPA(that's, multi-page applications), but you can still achieve SPA effect via js library, let's say, HTMX or Alpinejs.


# License

MIT

# Credit

to [WangFuqiang](https://afoo.me) @ [KEEVOL.com](https://keevol.com)
