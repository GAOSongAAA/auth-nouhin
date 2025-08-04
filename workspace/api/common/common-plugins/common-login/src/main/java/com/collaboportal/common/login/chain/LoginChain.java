package com.collaboportal.common.login.chain;

import com.collaboportal.common.login.context.LoginContext;
import com.collaboportal.common.login.handler.LoginHanlder;

public class LoginChain {

    private static class  HandlerLoginNode {

        LoginHanlder handler;
        HandlerLoginNode next;

        HandlerLoginNode(LoginHanlder handler) {
            this.handler = handler;
            this.next = null;
        }
    }

    private HandlerLoginNode head;
    private HandlerLoginNode tail;

    public LoginChain addHandler(LoginHanlder handler) {
        HandlerLoginNode newNode = new HandlerLoginNode(handler);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        return this;
    }

    public boolean execute(LoginContext context) {
        HandlerLoginNode current = head;
        while (current != null) {
            if (!current.handler.handle(context)) {
                return false;
            }
            current = current.next;

        }
        return true;
    }

    public boolean isEmpty() {
        return head == null;
    }

    

}
