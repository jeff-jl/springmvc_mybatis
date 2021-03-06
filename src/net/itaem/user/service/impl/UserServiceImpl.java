package net.itaem.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import net.itaem.base.entity.Page;
import net.itaem.privilege.entity.Privilege;
import net.itaem.privilege.service.IPrivilegeService;
import net.itaem.role.dao.IRoleDao;
import net.itaem.role.entity.RoleUser;
import net.itaem.user.dao.IUserDao;
import net.itaem.user.entity.User;
import net.itaem.user.entity.UserMenu;
import net.itaem.user.entity.UserPrivilege;
import net.itaem.user.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * user service实现类
 * @author luohong
 * @date 2014-12-24
 * @email 846705189@qq.com 
 * */
@Service
public class UserServiceImpl implements IUserService {
	@Autowired
	private IUserDao userDao;

	@Autowired
	private IRoleDao roleDao;

	@Autowired
	private IPrivilegeService privilegeService; 

	@Override
	public User exists(User user) {
		User u = userDao.exists(user);
		
		if(u != null){
			//设置用户角色列表
			u.setRoleList(roleDao.listByUserId(u.getId()));
			
			//设置用户权限列表
			List<Privilege> priList = privilegeService.listByUserId(u.getId());
			if(priList != null){
				List<Privilege> result = new ArrayList<Privilege>();
				for(Privilege pri: priList){
                    //result.add(pri);  //导航类别
                    if(pri.getChildren() != null){
                    	result.addAll(pri.getChildren());
                    }
				}
				u.setPrivilegeList(result);
			}
		}

		return u;
	}

	@Override
	public List<User> listByRoleId(String userId) {
		return userDao.listByRoleId(userId);
	}

	@Override
	public List<User> listAll(Page page) {
		return userDao.listAll(page);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
	@Override
	public void add(User user) {
		userDao.add(user);
		if(user.getRoleUsers() != null){
			for(RoleUser roleUser: user.getRoleUsers()){
				roleDao.addRoleUser(roleUser);
			}
		}
	}

	@Override
	public User listBy(String userId) {
		return userDao.listBy(userId);
	}

	@Transactional
	@Override
	public void addUserPrivilege(UserPrivilege userPri) {
		userDao.addUserPrivilege(userPri);
	}

	@Transactional
	@Override
	public void addUserMenu(UserMenu userMenu) {
		userDao.addUserMenu(userMenu);
	}

	@Transactional
	@Override
	public void deleteUserMenu(UserMenu userMenu) {
		userDao.deleteUserMenu(userMenu);
	}

	@Transactional
	@Override
	public void deleteUserPrivilege(UserPrivilege userPri) {
		userDao.deleteUserPrivilege(userPri);
	}

	@Transactional
	@Override
	public void deleteUserPrivileges(UserPrivilege[] userPris) {
		for(UserPrivilege userPri: userPris){
			deleteUserPrivilege(userPri);
		}
	}

	@Transactional
	@Override
	public void addUserPrivileges(UserPrivilege[] userPris) {
		for(UserPrivilege userPri: userPris){
			addUserPrivilege(userPri);
		}
	}

	@Override
	public int countAll() {
		return userDao.countAll();
	}

	@Override
	public boolean hasRegisted(String loginName) {
		return userDao.hasRegisted(loginName);
	}


	@Transactional
	@Override
	public void delete(String[] ids) {
		if(ids == null || ids.length == 0){
			return;
		}

		for(String id: ids){
			if(id.equals("root")){
				continue;
			}
			userDao.delete(id);
		}
	}

	@Override
	public void update(User user) {
		userDao.update(user);
		if(user.getRoleUsers() != null){
			roleDao.deleteRoleUserByUserId(user.getId());
			for(RoleUser roleUser: user.getRoleUsers()){
				roleDao.addRoleUser(roleUser);
			}
		}
	}
}
