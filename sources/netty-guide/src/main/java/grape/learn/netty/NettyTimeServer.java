package grape.learn.netty;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty 实现TimeServer
 *
 * @author grape
 * @date 2019-06-04
 */
public class NettyTimeServer {

  public static final String REQ = "QUERY SERVER TIME";

  public static void main(String[] args) {
    new NettyTimeServer().startServer(1080);
  }

  /**
   * 启动时间服务器
   *
   * @param port
   */
  public void startServer(int port) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    ServerBootstrap bootstrap =
        new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(new ChildChannelHandler());

    try {
      // 调用同步阻塞方法直到绑定成功
      ChannelFuture future = bootstrap.bind(port).sync();
      System.out.println("netty time server has started!");
      // 等待服务器端链路关闭后main函数退出
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      // 优雅退出
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
      ch.pipeline().addLast(new TimeServerHandler());
    }
  }

  private static class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ByteBuf buffer = (ByteBuf) msg;
      byte[] body = new byte[buffer.readableBytes()];
      buffer.readBytes(body);
      String req = new String(body, StandardCharsets.UTF_8);
      System.out.println("client send message : " + req);
      String rsp = "BAD REQUEST!";
      if (REQ.equalsIgnoreCase(req)) {
        rsp = LocalDateTime.now().toString();
      }

      ctx.write(Unpooled.copiedBuffer(rsp.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
      // 为提升性能 ctx.write 不会直接向 channel 写数据,它会先写入到buffer,
      // 调用 flush方法 将buffer中的数据 全部写到 channel中
      ctx.flush();
    }
  }
}
