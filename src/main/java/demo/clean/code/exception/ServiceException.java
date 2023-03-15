package demo.clean.code.exception;

import demo.clean.code.service.BaseService.BaseRequest;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@AllArgsConstructor
public class ServiceException extends RuntimeException {
    private static final String MESSAGE = ".message";
    protected BaseRequest request;
    protected String messageCode;

    public String getMessage() {
        return toMessage(messageCode);
    }

    protected String toMessage(String messageCode) {
        return toMessage(messageCode, null);
    }

    protected String toMessage(String messageCode, Object[] args) {
        // TODO add messageCode + message
        // TODO if not found return general error
        // TODO getMessage -> NoSuchMessageException

        return Optional.of(getMessageSource().getMessage(messageCode + MESSAGE, args, getLang()))
            .orElse(messageCode);

    }

    protected Locale getLang() {
     return Locale.ENGLISH;
    }

    static private MessageSource messageSource;
    static protected MessageSource getMessageSource() {
        if (Objects.isNull(messageSource)) {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:errors");
            messageSource.setDefaultEncoding("UTF-8");
            ServiceException.messageSource = messageSource;
        }
        return ServiceException.messageSource;
    }
}
