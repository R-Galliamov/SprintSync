package com.developers.sprintsync.data.workout_plan.mapper

import com.developers.sprintsync.data.workout_plan.dto.PlanDayDto
import com.developers.sprintsync.data.workout_plan.dto.WorkoutPlanDto
import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.domain.workouts_plan.model.PlanDay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WorkoutPlanMapperTest {

 @Test
 fun `maps full WorkoutPlanDto to domain and back without loss`() {
  val dto = WorkoutPlanDto(
   id = "plan1",
   startDate = 123456789L,
   owner = "userX",
   title = "My Plan",
   description = "This is a test plan",
   days = listOf(
    PlanDayDto.RestDto,
    PlanDayDto.WorkoutSessionDto(
     id = 42,
     runDay = 5,
     title = "Long run",
     description = "Run for 10km",
     targets = mapOf(
      "calories" to 800f,
      "distance" to 10f,
      "duration" to 60f
     )
    )
   )
  )

  val domain = dto.toDomain()
  val dtoBack = domain.toDto()

  assertThat(dtoBack.id).isEqualTo(dto.id)
  assertThat(dtoBack.startDate).isEqualTo(dto.startDate)
  assertThat(dtoBack.owner).isEqualTo(dto.owner)
  assertThat(dtoBack.title).isEqualTo(dto.title)
  assertThat(dtoBack.description).isEqualTo(dto.description)
  assertThat(dtoBack.days.size).isEqualTo(dto.days.size)
  // Сравнение каждого дня
  dtoBack.days.zip(dto.days).forEach { (actual, expected) ->
   when (expected) {
    is PlanDayDto.RestDto -> assertThat(actual).isInstanceOf(PlanDayDto.RestDto::class.java)
    is PlanDayDto.WorkoutSessionDto -> {
     actual as PlanDayDto.WorkoutSessionDto
     assertThat(actual.id).isEqualTo(expected.id)
     assertThat(actual.runDay).isEqualTo(expected.runDay)
     assertThat(actual.title).isEqualTo(expected.title)
     assertThat(actual.description).isEqualTo(expected.description)
     assertThat(actual.targets).isEqualTo(expected.targets)
    }
   }
  }
 }

 @Test
 fun `maps Rest-only plan correctly`() {
  val dto = WorkoutPlanDto(
   id = "rest-plan",
   startDate = 0L,
   owner = "userY",
   title = "Rest days",
   description = "Just rest",
   days = listOf(PlanDayDto.RestDto, PlanDayDto.RestDto)
  )

  val domain = dto.toDomain()
  assertThat(domain.days).allMatch { it is PlanDay.Rest }
  val dtoBack = domain.toDto()
  assertThat(dtoBack.days).allMatch { it is PlanDayDto.RestDto }
 }

 @Test
 fun `WorkoutSession targets correctly maps string metric names (case-insensitive)`() {
  val dto = PlanDayDto.WorkoutSessionDto(
   id = 77,
   runDay = 7,
   title = "Test run",
   description = "Description",
   targets = mapOf("Calories" to 300f, "distance" to 5.5f, "DURATION" to 45.0f)
  )

  val domain = dto.toDomain() as PlanDay.WorkoutSession
  assertThat(domain.targets[Metric.CALORIES]).isEqualTo(300f)
  assertThat(domain.targets[Metric.DISTANCE]).isEqualTo(5.5f)
  assertThat(domain.targets[Metric.DURATION]).isEqualTo(45.0f)

  val dtoBack = domain.toDto() as PlanDayDto.WorkoutSessionDto
  assertThat(dtoBack.targets.keys).containsExactlyInAnyOrder("calories", "distance", "duration")
 }

 @Test
 fun `unknown metric keys in dto are ignored`() {
  val dto = PlanDayDto.WorkoutSessionDto(
   id = 123,
   runDay = 10,
   title = "Interval",
   description = "Run fast",
   targets = mapOf("INVALID" to 10f, "duration" to 60f)
  )
  val domain = dto.toDomain() as PlanDay.WorkoutSession
  assertThat(domain.targets).doesNotContainKey(Metric.CALORIES)
  assertThat(domain.targets).doesNotContainKey(Metric.DISTANCE)
  assertThat(domain.targets[Metric.DURATION]).isEqualTo(60f)
 }

 @Test
 fun `handles empty targets gracefully`() {
  val dto = PlanDayDto.WorkoutSessionDto(
   id = 1,
   runDay = 1,
   title = "No targets",
   description = "",
   targets = emptyMap()
  )
  val domain = dto.toDomain() as PlanDay.WorkoutSession
  assertThat(domain.targets).isEmpty()
  val dtoBack = domain.toDto() as PlanDayDto.WorkoutSessionDto
  assertThat(dtoBack.targets).isEmpty()
 }

 @Test
 fun `maps empty days list correctly`() {
  val dto = WorkoutPlanDto(
   id = "empty",
   startDate = 1L,
   owner = "none",
   title = "Empty",
   description = "",
   days = emptyList()
  )
  val domain = dto.toDomain()
  assertThat(domain.days).isEmpty()
  val dtoBack = domain.toDto()
  assertThat(dtoBack.days).isEmpty()
 }

 @Test
 fun `maps list extension mappers correctly`() {
  val dtoList = listOf(
   WorkoutPlanDto(id = "a", title = "A"),
   WorkoutPlanDto(id = "b", title = "B")
  )
  val domainList = dtoList.toDomain()
  assertThat(domainList).hasSize(2)
  assertThat(domainList[0].id).isEqualTo("a")
  val dtoBackList = domainList.toDto()
  assertThat(dtoBackList[1].title).isEqualTo("B")
 }

 @Test
 fun `maps Flow of WorkoutPlanDto correctly`() = kotlinx.coroutines.test.runTest {
  val flow = flowOf(
   listOf(
    WorkoutPlanDto(id = "plan", title = "Title", days = emptyList())
   )
  )
  val domainFlow = flow.toDomain().first()
  assertThat(domainFlow[0].id).isEqualTo("plan")
  assertThat(domainFlow[0].title).isEqualTo("Title")
 }

 @Test
 fun `withId sets id on dto and list correctly`() {
  val dto = WorkoutPlanDto(id = "oldId", title = "X")
  val newDto = dto.withId("newId")
  assertThat(newDto.id).isEqualTo("newId")
  val dtoList = listOf(dto)
  val newList = dtoList.withId("anotherId")
  assertThat(newList[0].id).isEqualTo("anotherId")
 }
}
