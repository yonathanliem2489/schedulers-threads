package demo.clean.code.exception;


import com.tiket.tix.bus.model.common.constant.enums.ResponseCode;

public class BusinessLogicException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String code;
  private String message;


  public BusinessLogicException(String code, String message) {
    super();
    this.setCode(code);
    this.setMessage(message);
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "BusinessLogicException{" +
        "code='" + code + '\'' +
        ", message='" + message + '\'' +
        '}';
  }
}
