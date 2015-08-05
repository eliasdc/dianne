package be.iminds.iot.dianne.api.nn.module.dto;

import java.util.UUID;

/**
 * Represents an actual instance of a Neural Network module
 * 
 * Uniquely identified by the moduleId (which module is it), the frameworkId 
 * (where is it deployed) and nnId (which neural network instance does it belong to)
 * 
 * @author tverbele
 *
 */
public class ModuleInstanceDTO {

	// Module UUID of this module
	public final UUID moduleId;
	
	// UUID of the Neural Network this instance belongs to
	public final UUID nnId;
	
	// UUID of the framework where the module instance is deployed
	public final UUID frameworkId;

	// Module type
	public final String type;
	
	public ModuleInstanceDTO(UUID moduleId, UUID nnId, UUID frameworkId, String type){
		this.moduleId = moduleId;
		this.nnId = nnId;
		this.frameworkId = frameworkId;
		this.type = type;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ModuleInstanceDTO)){
			return false;
		}
		
		ModuleInstanceDTO other = (ModuleInstanceDTO) o;
		return other.moduleId.equals(moduleId)
				&&	other.nnId.equals(nnId)
				&&  other.frameworkId.equals(frameworkId);
	}
	
	@Override
	public int hashCode(){
		return moduleId.hashCode() + 31*nnId.hashCode() + frameworkId.hashCode();
	}
}