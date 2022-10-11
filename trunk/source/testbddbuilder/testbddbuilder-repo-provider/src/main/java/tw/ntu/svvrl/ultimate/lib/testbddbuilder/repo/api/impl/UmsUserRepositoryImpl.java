package tw.ntu.svvrl.ultimate.lib.testbddbuilder.repo.api.impl;


import tw.ntu.svvrl.ultimate.lib.testbddbuilder.repo.api.UmsUserRepository;
import cn.org.thinkcloud.repo.base.service.api.impl.BaseRepositoryImpl;
import org.apache.dubbo.config.annotation.DubboService;
import tw.ntu.svvrl.ultimate.lib.testbddbuilder.domain.UmsUser;
import tw.ntu.svvrl.ultimate.lib.testbddbuilder.repo.mapper.UmsUserMapper;

/**
 * @author : zhangqian9158@gmail.com
 */
@DubboService(version = "1.0.0")
public class UmsUserRepositoryImpl extends BaseRepositoryImpl<UmsUserMapper, UmsUser> implements UmsUserRepository {

}
