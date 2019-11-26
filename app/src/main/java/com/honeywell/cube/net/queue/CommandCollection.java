package com.honeywell.cube.net.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by H157925 on 16/5/28. 09:54
 * Email:Shodong.Sun@honeywell.com
 * <p/>
 * 用于组织发送的命令，WebSocket和Socket公用
 */
public class CommandCollection {
    private static final String TAG = CommandCollection.class.getSimpleName();

    //队列最大长度 超过则清零
    private static final int LIST_MAX_SIZE = 50;

    /**
     * 接口 进行队列的操作
     */
    public interface CommandList {
        void addCommand(Object command);

        Object removeCommand(Integer index);

        void clearCommand();

        boolean isEmpty();

        int count();
    }

    /**
     * 发送普通命令时的队列
     */
    public static class NormalCommandCollection implements CommandList {
        private static NormalCommandCollection instance = new NormalCommandCollection();
        private static List<Object> normalCommandList;

        private NormalCommandCollection() {
            normalCommandList = Collections.synchronizedList(new ArrayList<Object>());
        }

        public static NormalCommandCollection getInstance() {
            return instance;
        }

        @Override
        public void addCommand(Object command) {
            if (normalCommandList.size() > LIST_MAX_SIZE) {
                normalCommandList.clear();
            }
            normalCommandList.add(command);
        }

        @Override
        public Object removeCommand(Integer index) {
            if (normalCommandList.size() >= index) {
                Object hmiCommand = normalCommandList.get(index);
                normalCommandList.remove(hmiCommand);
                return hmiCommand;
            }
            return null;
        }

        @Override
        public void clearCommand() {
            normalCommandList.clear();
        }

        @Override
        public boolean isEmpty() {
            return normalCommandList.isEmpty();
        }

        @Override
        public int count() {
            return normalCommandList.size();
        }
    }

    /**
     * 用于发送Ping包的，实际上在这个项目Ping不需要设计成单独的队列，考虑到代码复用，也按照普通的队列设计
     */
    public static class PingCommandCollection implements CommandList {
        private static PingCommandCollection instance = new PingCommandCollection();
        private static List<Object> pingCommandList;

        private PingCommandCollection() {
            pingCommandList = Collections.synchronizedList(new ArrayList<Object>());
        }

        public static PingCommandCollection getInstance() {
            return instance;
        }

        @Override
        public void addCommand(Object command) {
            if (pingCommandList.size() > LIST_MAX_SIZE) {
                pingCommandList.clear();
            }
            pingCommandList.add(command);
        }

        @Override
        public Object removeCommand(Integer index) {
            if (pingCommandList.size() >= index) {
                Object hmiCommand = pingCommandList.get(index);
                pingCommandList.remove(hmiCommand);
                return hmiCommand;
            }
            return null;
        }

        @Override
        public void clearCommand() {
            pingCommandList.clear();
        }

        @Override
        public boolean isEmpty() {
            return pingCommandList.isEmpty();
        }

        @Override
        public int count() {
            return pingCommandList.size();
        }
    }

    /**
     * 接收线程队列
     */
    public static class ReceiverCommandCollection implements CommandList {
        private static ReceiverCommandCollection instance = new ReceiverCommandCollection();
        private static List<Object> receiverCommandList;

        private ReceiverCommandCollection() {
            receiverCommandList = Collections.synchronizedList(new ArrayList<Object>());
        }

        public static ReceiverCommandCollection getInstance() {
            return instance;
        }

        @Override
        public void addCommand(Object command) {
            receiverCommandList.add(command);
        }

        @Override
        public Object removeCommand(Integer index) {
            if (receiverCommandList.size() >= index) {
                Object hmiCommand = receiverCommandList.get(index);
                receiverCommandList.remove(hmiCommand);
                return hmiCommand;
            }
            return null;
        }

        @Override
        public void clearCommand() {
            receiverCommandList.clear();
        }

        @Override
        public boolean isEmpty() {
            return receiverCommandList.isEmpty();
        }

        @Override
        public int count() {
            return receiverCommandList.size();
        }
    }

}
