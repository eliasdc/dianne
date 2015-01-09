package be.iminds.iot.dianne.nn.module.activation;

import be.iminds.iot.dianne.nn.module.AbstractModule;

public class Tanh extends AbstractModule {

	public Tanh(){
		// TODO should we allocate tensor here?
		output = factory.createTensor(1);
	}
	
	@Override
	protected void forward() {
		if(!input.sameDim(output)){
			output = factory.createTensor(input.dims());
		}
		output = factory.getTensorMath().tanh(output, input);
	}

	@Override
	protected void backward() {
		// TODO check also if dim of output is same as gradoutput?
		if(!gradInput.sameDim(gradOutput)){
			gradInput = factory.createTensor(gradOutput.dims());
		}
		// derivative of tanh:
		// dtanh/dx = 1-tanh^2 
		//
		// thus:
		// gradInput = gradOutput * ( dtan/dx(input) )
		//           = gradOutput * (1 - tanh^2(input))
		//           = gradOutput * (1 - output^2)
		gradInput = factory.getTensorMath().cmul(gradInput, gradOutput, 
				factory.getTensorMath().dtanh(gradInput, output));
	}

}
