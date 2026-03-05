package com.shopsphere.product.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Pipeline;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class VisualSearchService {

    private ZooModel<Image, float[]> model;

    @PostConstruct
    public void init() throws Exception {
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(224, 224))
                .add(new ToTensor())
                .add(new Normalize(
                        new float[] {0.485f, 0.456f, 0.406f},
                        new float[] {0.229f, 0.224f, 0.225f}));

        Criteria<Image, float[]> criteria = Criteria.builder()
                .setTypes(Image.class, float[].class)
                .optArtifactId("resnet")
                .optTranslator(new Translator<Image, float[]>() {
                    @Override
                    public NDList processInput(TranslatorContext ctx, Image input) {
                        return pipeline.transform(new NDList(input.toNDArray(ctx.getNDManager())));
                    }

                    @Override
                    public float[] processOutput(TranslatorContext ctx, NDList list) {
                        // FIXED: use toFloatArray() for version 0.21.0
                        return list.get(0).toFloatArray();
                    }

                    @Override
                    public Batchifier getBatchifier() {
                        return Batchifier.STACK;
                    }
                })
                .build();

        this.model = criteria.loadModel();
    }

    public List<Double> extractFeatures(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream();
             Predictor<Image, float[]> predictor = model.newPredictor()) {
            
            Image img = ImageFactory.getInstance().fromInputStream(is);
            float[] result = predictor.predict(img);
            
            List<Double> featureVector = new ArrayList<>();
            for (float f : result) {
                featureVector.add((double) f);
            }
            return featureVector;
        }
    }
}