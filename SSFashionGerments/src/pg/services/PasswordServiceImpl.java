/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pg.services;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import noticeModel.noticeModel;
import pg.OrganizationModel.OrganizationInfo;
import pg.dao.PasswordDAO;
import pg.exception.UserBlockedException;
import pg.model.Login;
import pg.model.Menu;
import pg.model.Module;
import pg.model.Password;
import pg.model.WareInfo;

@Service
public class PasswordServiceImpl implements PasswordService {
	@Autowired
	private PasswordDAO passDAO;

	@Transactional
	public List<Login> login(String username, String password) throws UserBlockedException {
		
		return passDAO.login(username,password);

	}

	@Transactional
	public List<WareInfo> getAllStoreName() {
		// TODO Auto-generated method stub
		return passDAO.getAllStoreName();
	}
	
	@Transactional
	public List<Module> getAllModuleName() {
		// TODO Auto-generated method stub
		return passDAO.getAllModuleName();
	}
	
	@Transactional
	public List<Menu> getAllMenuName() {
		// TODO Auto-generated method stub
		return passDAO.getAllMenuName();
	}
	
	@Transactional
	public List<Module> getUserModule(int i) {
		// TODO Auto-generated method stub
		return passDAO.getUserModule(i);
	}	
	@Transactional
	public List<Menu> getUserMenu(int i,int moduleId) {

		return passDAO.getUserMenu(i,moduleId);
	}
	@Override
	public List<Menu> getAdminUserMenu(int i, int moduleId) {
		// TODO Auto-generated method stub
		return passDAO.getAdminUserMenu(i, moduleId);
	}


	@Override
	public boolean changePassword(String userId, String userName, String password) {
		// TODO Auto-generated method stub
		return passDAO.changePassword(userId, userName, password);
	}


	@Override
	public List<OrganizationInfo> getOrganizationInfo() {
		// TODO Auto-generated method stub
		return passDAO.getOrganizationInfo();
	}

	@Override
	public String getUserDepartmentId(String userId) {
		// TODO Auto-generated method stub
		return passDAO.getUserDepartmentId(userId);
	}

	@Override
	public List<noticeModel> getNotice(String depid,noticeModel nm) {
		// TODO Auto-generated method stub
		return passDAO.getNotice(depid, nm);
	}

	@Override
	public JSONArray getRolePermissions(String roleIds) {
		// TODO Auto-generated method stub
		return passDAO.getRolePermissions(roleIds);
	}

	@Override
	public String saveUserProfile(String userInfo) {
		// TODO Auto-generated method stub
		return passDAO.saveUserProfile(userInfo);
	}

	@Override
	public JSONArray getUserList() {
		// TODO Auto-generated method stub
		return passDAO.getUserList();
	}

	@Override
	public JSONObject getUserInfo(String userId) {
		// TODO Auto-generated method stub
		return passDAO.getUserInfo(userId);
	}

	@Override
	public String editUserProfile(String userInfo) {
		// TODO Auto-generated method stub
		return passDAO.editUserProfile(userInfo);
	}
}