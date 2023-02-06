package ${packageName}.${basePackageName};

/**
 * <p>
 * Page基类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
public class BasePage {

    public static final int DEFAULT_PAGE_NUM = 1;

    public static final int DEFAULT_PAGE_SIZE = 10;

    private Integer pageNum;

    private Integer pageSize;

    private String orderBy;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
