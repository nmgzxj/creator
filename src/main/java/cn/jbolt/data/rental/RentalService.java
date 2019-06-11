package cn.jbolt.data.rental;

import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.Rental;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

public class RentalService extends BaseService<Rental> {

    public static final RentalService me = new RentalService();
    @Inject
    private SystemLogService systeLogService;
//	private Area dao = new Area().dao();

    @Override
    protected Rental dao() {
        return Rental.dao;
    }

    public Page<Rental> paginate(int pageNum, String keywords, String hasTaxControl, String companyAttrib, String payType) {
        String queryString = " where 1=1 ";
        if (StrKit.notBlank(keywords)) {
            queryString += " and (tax_id like '%" + keywords + "%' or company_name like '%" + keywords + "%' " +
                    "or reg_address like '%" + keywords + "%' or representative_name like '%" + keywords + "%' or representative_phone like '%" + keywords + "%'" +
                    " or agent_name_phone like '%" + keywords + "%' or address_from like '%" + keywords + "%') ";
        }
        if (StrKit.notBlank(hasTaxControl)) {
            queryString += " and has_tax_control="+hasTaxControl;
        }
        if (StrKit.notBlank(companyAttrib)) {
            queryString += " and company_attrib="+companyAttrib;
        }
        if (StrKit.notBlank(payType)) {
            queryString += " and pay_type="+payType;
        }
        return dao().paginate(pageNum, PropKit.getInt("pageSize"), "select *", "from rental " + queryString + " order by id desc");
    }

    public Rental findById(int id) {
        return dao().findById(id);
    }

    public Rental edit(int id) {
        return dao().findById(id);
    }

    public Ret save(Rental bean) {
        bean.save();
        return Ret.ok();
    }

    /**
     * 修改数据
     */
    public Ret update(Rental bean, int userId) {
        Ret ret = new Ret();
        if (bean.update()) {
            ret.setOk();
            ret.put("msg", "更新成功");
        } else {
            ret.setFail();
            ret.put("msg", "更新失败");
        }
        return ret;
    }

    /**
     * 删除数据
     */
    public Ret delete(final int id, int userId) {
        Rental bean = dao().findById(id);
        if (bean.delete()) {

            return Ret.ok("msg", "删除成功");
        } else {
            return Ret.fail("msg", "删除失败");
        }
    }
}