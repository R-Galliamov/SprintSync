package com.developers.sprintsync.user.model.chart.chartData

typealias MetricsMap = Map<Metric, Float>
typealias TimestampMetricsMap = Map<Long, MetricsMap>
typealias IndexedDailyValues = Map<Int, DailyValues>
typealias MetricToDailyValues = Map<Metric, IndexedDailyValues>
