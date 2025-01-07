package com.developers.sprintsync.statistics.domain.chart.data

import com.developers.sprintsync.core.components.goal.data.model.Metric

typealias MetricsMap = Map<Metric, Float>
typealias TimestampMetricsMap = Map<Long, MetricsMap>
typealias MetricToDailyValues = Map<Metric, List<DailyValues>>
