package musiclearning.extension

import com.musicg.wave.Wave;

class WaveExtension {
	public static def getReducedNormalizedAmplitudes(Wave wave, float timeStep){
		// for signed signals, the middle is 0 (-1 ~ 1)
		def ret = [];
		double middleLine=0;

		// usually 8bit is unsigned
		if (wave.getWaveHeader().getBitsPerSample()==8){
			// for unsigned signals, the middle is 0.5 (0~1)
			middleLine=0.5;
		}

		double[] nAmplitudes = wave.getNormalizedAmplitudes();
		int width = (int) (nAmplitudes.length / wave.getWaveHeader().getSampleRate() / timeStep);
		int height = 500;
		int middle = height / 2;
		int magnifier = 1000;

		int numSamples = nAmplitudes.length;

		if (width>0){
			int numSamplePerTimeFrame = numSamples / width;

			int[] scaledPosAmplitudes = new int[width];

			// width scaling
			for (int i = 0; i < width; i++) {
				double sumPosAmplitude = 0;
				int startSample=i * numSamplePerTimeFrame;
				for (int j = 0; j < numSamplePerTimeFrame; j++) {
					double a = nAmplitudes[startSample + j];
					if (a > middleLine) {
						sumPosAmplitude += (a-middleLine);
					}
				}

				int scaledPosAmplitude = (int) (sumPosAmplitude
												/ numSamplePerTimeFrame * magnifier + middle);

				scaledPosAmplitudes[i] = scaledPosAmplitude-250;
			}

			ret = scaledPosAmplitudes;
			return ret;
		}
	}
}
