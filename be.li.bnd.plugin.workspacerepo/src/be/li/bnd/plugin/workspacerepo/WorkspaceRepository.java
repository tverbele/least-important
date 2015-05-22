package be.li.bnd.plugin.workspacerepo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.naming.OperationNotSupportedException;

import aQute.bnd.build.Workspace;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.version.Version;
import aQute.service.reporter.Reporter;

public class WorkspaceRepository implements Plugin, RepositoryPlugin{

	public static final String PROP_LOCATION = "location";
	
	Reporter reporter;
	
	private String location;
	private aQute.bnd.build.WorkspaceRepository repository;
	
	@Override
	public void setProperties(Map<String, String> map) throws Exception {
		location = map.get(PROP_LOCATION);
		
		File wsDir = new File(location);
		if(!wsDir.exists() || !wsDir.isDirectory()){
			if(reporter!=null)
				reporter.error("No valid directory : %s", location);
		}
		try {
			Workspace ws = new Workspace(wsDir);
			repository = ws.getWorkspaceRepository();
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
		return repository.get(bsn, version, properties, listeners);
	}

	@Override
	public boolean canWrite() {
		return false;
	}

	@Override
	public List<String> list(String pattern) throws Exception {
		return repository.list(pattern);
	}

	@Override
	public SortedSet<Version> versions(String bsn) throws Exception {
		return repository.versions(bsn);
	}

	@Override
	public String getName() {
		return repository.getName();
	}

	@Override
	public String getLocation() {
		return repository.getLocation();
	}

}
