//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package demo.clean.code.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BaseResponse<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private String code;
  private String message;
  private List<String> errors;
  private T data;
  private Date serverTime;

  public BaseResponse() {
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<String> getErrors() {
    return this.errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  public T getData() {
    return this.data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Date getServerTime() {
    return this.serverTime;
  }

  public void setServerTime(Date serverTime) {
    this.serverTime = serverTime;
  }

  public String toString() {
    return "BaseResponse{code='" + this.code + '\'' + ", message='" + this.message + '\'' + ", errors=" + this.errors + ", data=" + this.data + ", serverTime=" + this.serverTime + '}';
  }
}
