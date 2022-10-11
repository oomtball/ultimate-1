package tw.ntu.svvrl.ultimate.lib.bddbuilder.repo.api.impl;


import tw.ntu.svvrl.ultimate.lib.bddbuilder.repo.api.UmsUserRepository;
import cn.org.thinkcloud.repo.base.service.api.impl.BaseRepositoryImpl;
import org.apache.dubbo.config.annotation.DubboService;
import tw.ntu.svvrl.ultimate.lib.bddbuilder.domain.UmsUser;
import tw.ntu.svvrl.ultimate.lib.bddbuilder.repo.mapper.UmsUserMapper;

/**
 * @author : zhangqian9158@gmail.com
 */
@DubboService(version = "1.0.0")
public class UmsUserRepositoryImpl extends BaseRepositoryImpl<UmsUserMapper, UmsUser> implements UmsUserRepository {

}
