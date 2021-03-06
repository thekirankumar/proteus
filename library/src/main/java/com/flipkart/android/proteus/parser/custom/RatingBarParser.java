package com.flipkart.android.proteus.parser.custom;

import android.graphics.drawable.Drawable;

import com.flipkart.android.proteus.parser.Attributes;
import com.flipkart.android.proteus.parser.ParseHelper;
import com.flipkart.android.proteus.parser.Parser;
import com.flipkart.android.proteus.parser.WrappableParser;
import com.flipkart.android.proteus.processor.DimensionAttributeProcessor;
import com.flipkart.android.proteus.processor.DrawableResourceProcessor;
import com.flipkart.android.proteus.processor.StringAttributeProcessor;
import com.flipkart.android.proteus.view.FixedRatingBar;
import com.google.gson.JsonElement;

/**
 * Created by kiran.kumar on 12/05/14.
 */
public class RatingBarParser<T extends FixedRatingBar> extends WrappableParser<T> {

    public RatingBarParser(Parser<T> wrappedParser) {
        super(FixedRatingBar.class, wrappedParser);
    }

    @Override
    protected void prepareHandlers() {
        super.prepareHandlers();
        addHandler(Attributes.RatingBar.NumStars, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setNumStars(ParseHelper.parseInt(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.Rating, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setRating(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.IsIndicator, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setIsIndicator(ParseHelper.parseBoolean(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.StepSize, new StringAttributeProcessor<T>() {
            @Override
            public void handle(String attributeKey, String attributeValue, T view) {
                view.setStepSize(ParseHelper.parseFloat(attributeValue));
            }
        });
        addHandler(Attributes.RatingBar.MinHeight, new DimensionAttributeProcessor<T>() {
            @Override
            public void setDimension(float dimension, T view, String key, JsonElement value) {
                view.setMinimumHeight((int) dimension);
            }
        });
        addHandler(Attributes.RatingBar.ProgressDrawable, new DrawableResourceProcessor<T>() {
            @Override
            public void setDrawable(T view, Drawable drawable) {
                drawable = view.getTiledDrawable(drawable, false);
                view.setProgressDrawable(drawable);
            }
        });
    }
}
