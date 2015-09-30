package com.dianping.pigeon.governor.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.pigeon.governor.bean.JqGridRespBean;
import com.dianping.pigeon.governor.bean.ProjectBean;
import com.dianping.pigeon.governor.dao.ProjectMapper;
import com.dianping.pigeon.governor.model.Project;
import com.dianping.pigeon.governor.model.ProjectExample;
import com.dianping.pigeon.governor.model.User;
import com.dianping.pigeon.governor.service.ProjectService;
import com.dianping.pigeon.governor.service.UserService;
import com.dianping.pigeon.governor.util.CmdbUtils;

/**
 * 
 * @author chenchongze
 *
 */
@Service
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private UserService userService;
	
	@Override
	public int create(ProjectBean projectBean) {
		int sqlSucCount = -1;
		Project project = projectBean.createProject();
		
		if(StringUtils.isNotBlank(project.getName())) {
			Date now = new Date();
			project.setCreatetime(now);
			project.setModifytime(now);
			sqlSucCount = projectMapper.insertSelective(project);
		}
		
		return sqlSucCount;
	}


	@Override
	public int deleteByIdSplitByComma(String idsComma) {
		int sqlSucCount = 0;
		String idsArr[] = idsComma.split(",");
		
		for(String ids : idsArr){
			int id;
			int count = 0;
			
			try {
				id = Integer.parseInt(ids);
				count = projectMapper.deleteByPrimaryKey(id);;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}finally{
				sqlSucCount += count;
			}
			
		}
		
		return sqlSucCount;
	}


	@Override
	public int updateById(ProjectBean projectBean) {
		int sqlSucCount = -1;
		Project project = projectBean.convertToProject();
		
		if(StringUtils.isNotBlank(project.getName())
						&& project.getId() != null)
		{
			Date now = new Date();
			project.setModifytime(now);
			//TODO 这里修改用selective保持null就不更新，只更新有值部分，createtime和id不要更新
			sqlSucCount = projectMapper.updateByPrimaryKeySelective(project);
		}
		
		return sqlSucCount;
	}

	@Override
	public JqGridRespBean retrieveByJqGrid(int page, int rows) {
		JqGridRespBean jqGridTableBean = null;
		
		if(page > 0){
			List<Project> projects = projectMapper.selectByPageAndRows((page - 1) * rows, rows);
			int totalRecords = projectMapper.countByExample(null);
			int totalPages = (totalRecords - 1) / rows + 1;
			
			jqGridTableBean = new JqGridRespBean();
			jqGridTableBean.setData(projects);
			jqGridTableBean.setCurrentPage(page);
			jqGridTableBean.setTotalRecords(totalRecords);
			jqGridTableBean.setTotalPages(totalPages);
		}
		
		return jqGridTableBean;
	}


	@Override
	public Project findProject(String name) {
		Project result = null;
		
		if (StringUtils.isBlank(name)) {
			return result;
		}
		
		
		ProjectExample example = new ProjectExample();
		example.createCriteria().andNameEqualTo(name);
		List<Project> projects = projectMapper.selectByExample(example);
		
		if(projects != null){
			
			if(projects.size() > 0){
				result = projects.get(0);
			}
				
		}
		
		return result;
	}


	@Override
	public Project createProject(String projectName, boolean fromCmdb) {
		Project result = null;
		
		if(StringUtils.isBlank(projectName)) {
			return result;
		}
		
		Project project = null;
		
		if(fromCmdb) {
			project = CmdbUtils.getProjectInfo(projectName);
		} else {
			project = new Project();
			project.setName(projectName);
	    	Date now = new Date();
			project.setCreatetime(now);
			project.setModifytime(now);
		}
		
		projectMapper.insertSelective(project);
		
		ProjectExample projectExample = new ProjectExample();
		projectExample.createCriteria().andNameEqualTo(projectName);
		List<Project> projects = projectMapper.selectByExample(projectExample);
		
		if(projects != null){
			
			if(projects.size() > 0){
				result = projects.get(0);
			}
				
		}
		
		return result;
	}


	@Override
	public JqGridRespBean retrieveByJqGrid(int page, int rows,
			String projectOwner) {
		JqGridRespBean jqGridTableBean = null;
		
		if(page > 0){
			User user = userService.retrieveByDpaccount(projectOwner);
			
			if(user != null){
				Integer ownerId = user.getId();
				List<Project> projects = projectMapper.selectByPageRowsOwnerId((page - 1) * rows, rows, ownerId);
				int totalRecords = projectMapper.countByOwnerId(ownerId);
				int totalPages = (totalRecords - 1) / rows + 1;
				
				jqGridTableBean = new JqGridRespBean();
				jqGridTableBean.setData(projects);
				jqGridTableBean.setCurrentPage(page);
				jqGridTableBean.setTotalRecords(totalRecords);
				jqGridTableBean.setTotalPages(totalPages);
			}
			
		}
		
		return jqGridTableBean;
	}

}
