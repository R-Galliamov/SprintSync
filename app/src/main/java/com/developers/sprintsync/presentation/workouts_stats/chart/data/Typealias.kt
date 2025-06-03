package com.developers.sprintsync.presentation.workouts_stats.chart.data

import com.developers.sprintsync.domain.goal.model.Metric

typealias MetricsMap = Map<Metric, Float>
typealias TimestampMetricsMap = Map<Long, MetricsMap>
typealias MetricToDailyValues = Map<Metric, List<DailyValues>>
