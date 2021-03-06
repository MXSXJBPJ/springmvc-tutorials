package com.mindflow.servlet3.demo.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ricky Fung
 */
@WebServlet(urlPatterns = "/foo", asyncSupported = true)
public class FooServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final int seq = counter.getAndIncrement();
        logger.info("收到请求:{}", seq);
        final AsyncContext context = req.startAsync();
        context.setTimeout(10*1000);
        //1.开启异步线程
        context.start(new Runnable() {
            @Override
            public void run() {

                int time = (int) (Math.random() * 5000);
                logger.info("请求seq:{} 休眠{} ms", seq, time);
                try {
                    sleep(time);
                    logger.info("请求seq:{} 休眠结束，返回结果", seq);
                    context.getResponse().getWriter().write("success "+seq);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    context.complete();
                }

            }
        });
    }

    private void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
