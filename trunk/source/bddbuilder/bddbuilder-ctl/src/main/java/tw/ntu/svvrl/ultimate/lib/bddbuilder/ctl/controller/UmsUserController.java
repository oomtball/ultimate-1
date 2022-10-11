package tw.ntu.svvrl.ultimate.lib.bddbuilder.ctl.controller;

import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.org.thinkcloud.base.commons.domain.Domain;
import cn.org.thinkcloud.commons.controller.BaseController;
import tw.ntu.svvrl.ultimate.lib.bddbuilder.biz.api.UmsUserService;
import tw.ntu.svvrl.ultimate.lib.bddbuilder.domain.UmsUser;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ZHANG.Q
 * @since 2021-10-28
 */
@Api(tags = "框架示例")
@RestController
@RequestMapping("/v1/ums/user")
public class UmsUserController extends BaseController<UmsUser, UmsUserService> {

    @DubboReference(version = "1.0.0")
    private UmsUserService umsUserService;

    @Override
    protected Domain getUser() {
        return new Domain(1000L);
    }
}
