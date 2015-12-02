package be.iminds.iot.dianne.nn.module.layer;

import java.util.UUID;

import be.iminds.iot.dianne.api.nn.module.AbstractTrainableModule;
import be.iminds.iot.dianne.tensor.Tensor;
import be.iminds.iot.dianne.tensor.TensorFactory;

public class SpatialConvolution extends AbstractTrainableModule {

	private int noInputPlanes;
	private int noOutputPlanes;
	private int kernelWidth;
	private int kernelHeight;
	private int strideX;
	private int strideY;
	private int padX = 0;
	private int padY = 0;
	
	// subtensors for weights / bias
	Tensor weights;
	Tensor deltaWeights;
	Tensor bias;
	Tensor deltaBias;
	
	public SpatialConvolution(TensorFactory factory,
			int noInputPlanes, int noOutputPlanes, 
			int kernelWidth, int kernelHeight,
			int strideX, int strideY, boolean pad){
		super(factory);
		initParameters(noInputPlanes, noOutputPlanes, kernelWidth, kernelHeight, strideX, strideY, pad);
	}
	
	public SpatialConvolution(TensorFactory factory, UUID id,
			int noInputPlanes, int noOutputPlanes, 
			int kernelWidth, int kernelHeight,
			int strideX, int strideY, boolean pad){
		super(factory, id);
		initParameters(noInputPlanes, noOutputPlanes, kernelWidth, kernelHeight, strideX, strideY, pad);
	}
	
	protected void initParameters(int noInputPlanes, int noOutputPlanes, 
			int kernelWidth, int kernelHeight, int strideX, int strideY, boolean pad){
		this.noInputPlanes = noInputPlanes;
		this.noOutputPlanes = noOutputPlanes;
		this.kernelWidth = kernelWidth;
		this.kernelHeight = kernelHeight;
		this.strideX = strideX;
		this.strideY = strideY;
		if(pad){
			this.padX = (kernelWidth-1)/2;
			this.padY = (kernelHeight-1)/2;
		}
		
		parameters = factory.createTensor(noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight+noOutputPlanes);
		weights = parameters.narrow(0, 0, noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight);
		weights.reshape(noOutputPlanes, noInputPlanes, kernelWidth, kernelHeight);
		bias = parameters.narrow(0, noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight, noOutputPlanes);
		
		parameters.fill(0.0f);
	}
	
	protected void initDeltaParameters(){
		deltaParameters = factory.createTensor(noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight+noOutputPlanes);
		deltaWeights = deltaParameters.narrow(0, 0, noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight);
		deltaWeights.reshape(noOutputPlanes, noInputPlanes, kernelWidth, kernelHeight);
		deltaBias = deltaParameters.narrow(0, noOutputPlanes*noInputPlanes*kernelWidth*kernelHeight, noOutputPlanes);
		
		deltaParameters.fill(0.0f);
	}
	
	@Override
	public void randomize(){
		// randomize weights uniform [-std, std] with std = 1/sqrt(kW*kH*noInputPlanes)  [from torch]
		parameters.rand();
		float std = (float) (1f/Math.sqrt(kernelWidth*kernelHeight*noInputPlanes));
		parameters = factory.getTensorMath().mul(parameters, parameters, 2*std);
		parameters = factory.getTensorMath().sub(parameters, parameters, std);
	}
	
	@Override
	protected void forward() {
		// TODO check input planes dim?
		// TODO check kernel sizes?
		
		output = factory.getTensorMath().spatialconvolve(output, bias, input, weights, strideX, strideY, padX, padY);
	}

	@Override
	protected void backward() {
		if(deltaParameters==null){
			initDeltaParameters();
		}

		gradInput = factory.getTensorMath().spatialdinconvolve(gradInput, gradOutput, weights, strideX, strideY, padX, padY);
	}

	@Override
	public void accGradParameters() {
		deltaWeights = factory.getTensorMath().spatialdkerconvolve(deltaWeights, deltaWeights, gradOutput, input, strideX, strideY, padX, padY);
		
		for(int i = 0; i < noOutputPlanes; i++)
			deltaBias.set(factory.getTensorMath().sum(gradOutput.select(0, i)), i);
	}
}
