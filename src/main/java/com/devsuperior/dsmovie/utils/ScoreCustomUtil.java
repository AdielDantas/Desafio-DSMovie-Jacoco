package com.devsuperior.dsmovie.utils;

import com.devsuperior.dsmovie.entities.ScoreEntity;

import java.util.List;

public class ScoreCustomUtil {

    // Média das avaliações
    public static double calculateAverage(List<ScoreEntity> scores) {
        if (scores == null || scores.isEmpty()) return 0.0;

        double sum = 0.0;
        for (ScoreEntity score : scores) {
            sum += score.getValue();
        }

        return sum / scores.size();
    }

    // Total de avaliações
    public static int getScoreCount(List<ScoreEntity> scores) {
        if (scores == null) {
            return 0;
        } else {
            return scores.size();
        }
    }
}
