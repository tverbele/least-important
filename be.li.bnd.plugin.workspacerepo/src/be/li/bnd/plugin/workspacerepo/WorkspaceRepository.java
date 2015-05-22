package be.li.bnd.plugin.workspacerepo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.naming.OperationNotSupportedException;

import aQute.bnd.build.Workspace;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.version.Version;
import aQute.service.reporter.Reporter;

public class WorkspaceRepository implements Plugin, RepositoryPlugin{

	public static final String PROP_LOCATION = "location";
	public static final String PROP_NAME = "name";
	
	Reporter reporter;
	
	private String location;
	private String name;
	private List<RepositoryPlugin> repos = new ArrayList<>();
	
	@Override
	public void setProperties(Map<String, String> map) throws Exception {
		name = map.get(PROP_NAME);
		location = map.get(PROP_LOCATION);
		
		File wsDir = new File(location);
		if(!wsDir.exists() || !wsDir.isDirectory()){
			if(reporter!=null)
				reporter.error("No valid directory : %s", location);
		}
		try {
			Workspace ws = new Workspace(wsDir);

			// TODO which repositories to back? only the workspace, also local, all? 
			repos.add(ws.getWorkspaceRepository());
			repos.add(ws.getRepository("Local"));
			ws.close();
		} catch(Exception e){
			e.printStackTrace();
			if(reporter!=null)
				reporter.exception(e, "Error creating workspace : %s", location);
		}
	}

	@Override
	public void setReporter(Reporter r) {
		this.reporter = r;
	}
	
	
	@Override
	public PutResult put(InputStream stream, PutOptions options)
			throws Exception {
		throw new OperationNotSupportedException();
	}

	@Override
	public File get(String bsn, Version version,
			Map<String, String> properties, DownloadListener... listeners)
			throws Exception {
		File result = null;
		for(RepositoryPlugin r : repos){
			result = r.get(bsn, version, properties, listeners);
			if(result!=null)
				break;
		}
		return result;
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	@Override
	public List<String> list(String pattern) throws Exception {
		List<String> list = new ArrayList<>();
		for(RepositoryPlugin r : repos){
			list.addAll(r.list(pattern));
		}
		return list;
	}

	@Override
	public SortedSet<Version> versions(String bsn) throws Exception {
		SortedSet<Version> versions = new TreeSet<>();
		for(RepositoryPlugin r : repos){
			versions.addAll(r.versions(bsn));
		}
		return versions;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}

}
