package com.ircnet.service.clis.event;

import com.ircnet.common.library.event.AbstractEventListener;
import com.ircnet.service.clis.strategy.SQueryCommand;
import com.ircnet.service.library.IRCService;
import com.ircnet.service.library.events.SQueryEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Event for SQUERY message:
 *  :nick!user@localhost SQUERY Clis@irc.ircnet.com :list #irc
 *
 * SQUERY is used by users to talk to services. Services reply by NOTICE.
 *
 * This event listener parses all supported commands of Clis.
 */
@Component
public class SQueryEventListener extends AbstractEventListener<SQueryEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQueryEventListener.class);

    @Autowired
    private IRCService ircService;

    @Value("${service.name}")
    private String serviceName;

    @Resource(name = "squeryCommandMap")
    private Map<String, SQueryCommand> squeryCommandMap;

    public SQueryEventListener() {
    }

    protected void onEvent(SQueryEvent event) {
        String nick = event.getFrom().getNick();

        if (StringUtils.isEmpty(event.getMessage())) {
            return;
        }

        String[] parts = event.getMessage().split(" ");

        SQueryCommand squeryCommand = squeryCommandMap.get(parts[0]);

        if(squeryCommand != null) {
            squeryCommand.processCommand(event.getFrom(), event.getMessage());
        }

        else {
            ircService.notice(nick, "Unrecognized command: \"%s\". Use /SQUERY %s HELP\n", parts[0], serviceName);
        }
    }
}
