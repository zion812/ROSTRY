package com.rio.rostry.ui.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.ui.monitoring.vm.VaccinationViewModel
import com.rio.rostry.ui.components.PieChartView
import com.rio.rostry.ui.theme.LocalSpacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog

@Composable
fun VaccinationScheduleScreen(vm: VaccinationViewModel = hiltViewModel()) {
    val search = remember { mutableStateOf("") }
    val sp = LocalSpacing.current
    val state by vm.ui.collectAsState()
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        verticalArrangement = Arrangement.spacedBy(sp.sm)
    ) {
        Text("Vaccination Schedule", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = search.value, onValueChange = { search.value = it }, label = { Text("Search fowl or batch") })

        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
            OutlinedButton(onClick = { vm.setDays(7) }) { Text("7d") }
            OutlinedButton(onClick = { vm.setDays(30) }) { Text("30d") }
            OutlinedButton(onClick = { vm.setDays(90) }) { Text("90d") }
            OutlinedButton(onClick = { vm.refresh() }) { Text("Refresh") }
        }

        if (state.isLoading) {
            Row { CircularProgressIndicator() }
        }
        state.error?.let { err ->
            Text(text = err, color = MaterialTheme.colorScheme.error)
        }

        // Stats
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Last ${state.days} days", style = MaterialTheme.typography.titleMedium)
                val s = state.stats
                Text("Scheduled: ${s?.totalScheduled ?: 0}")
                Text("Administered: ${s?.totalAdministered ?: 0}")
                Text("Overdue: ${s?.overdueCount ?: 0}")
                Text("Coverage: ${s?.coveragePct?.let { "%.1f".format(it) } ?: "0.0"}%")
            }
        }

        // Distribution by vaccine type
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Administered by Vaccine Type")
                PieChartView(data = state.distribution)
            }
        }

        // Quick record form
        val ctx = LocalContext.current
        val quickProductId = remember { mutableStateOf("") }
        val quickVaccine = remember { mutableStateOf("") }
        val quickDose = remember { mutableStateOf("") }
        val quickCost = remember { mutableStateOf("") }
        val scheduledAt = remember { mutableStateOf<Long?>(null) }
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Quick record")
                OutlinedTextField(value = quickProductId.value, onValueChange = { quickProductId.value = it }, label = { Text("Product ID") })
                OutlinedTextField(value = quickVaccine.value, onValueChange = { quickVaccine.value = it }, label = { Text("Vaccine Type") })
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    OutlinedTextField(value = quickDose.value, onValueChange = { quickDose.value = it }, label = { Text("Dose (ml)") })
                    OutlinedTextField(value = quickCost.value, onValueChange = { quickCost.value = it }, label = { Text("Cost (INR)") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    val human = scheduledAt.value?.let { sdf.format(Date(it)) } ?: "Not set"
                    Text("Scheduled: $human")
                    OutlinedButton(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(ctx, { _, y, m, d ->
                            TimePickerDialog(ctx, { _, hour, min ->
                                val c = Calendar.getInstance()
                                c.set(Calendar.YEAR, y)
                                c.set(Calendar.MONTH, m)
                                c.set(Calendar.DAY_OF_MONTH, d)
                                c.set(Calendar.HOUR_OF_DAY, hour)
                                c.set(Calendar.MINUTE, min)
                                c.set(Calendar.SECOND, 0)
                                c.set(Calendar.MILLISECOND, 0)
                                scheduledAt.value = c.timeInMillis
                              }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
                            ).show()
                          }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) { Text("Pick date & time") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                    OutlinedButton(onClick = {
                        val pid = quickProductId.value.trim()
                        val vac = quickVaccine.value.trim()
                        if (pid.isNotEmpty() && vac.isNotEmpty()) {
                            val whenMs = scheduledAt.value ?: System.currentTimeMillis()
                            vm.schedule(
                                productId = pid,
                                vaccineType = vac,
                                scheduledAt = whenMs,
                                doseMl = quickDose.value.toDoubleOrNull(),
                                costInr = quickCost.value.toDoubleOrNull()
                            )
                            scheduledAt.value = null
                        }
                    }) { Text("Schedule") }
                    Button(onClick = {
                        val pid = quickProductId.value.trim()
                        val vac = quickVaccine.value.trim()
                        if (pid.isNotEmpty() && vac.isNotEmpty()) {
                            vm.recordAdministeredNow(
                                productId = pid,
                                vaccineType = vac,
                                doseMl = quickDose.value.toDoubleOrNull(),
                                costInr = quickCost.value.toDoubleOrNull()
                            )
                            scheduledAt.value = null
                        }
                    }) { Text("Record now") }
                }
            }
        }

        // Due reminders list (global, top few)
        ElevatedCard {
            Column(Modifier.padding(sp.sm), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
                Text("Due now (all products)")
                if (state.due.isEmpty()) {
                    Text("No vaccinations due at this time.")
                } else {
                    state.due.take(10).forEach { r ->
                        Row(horizontalArrangement = Arrangement.spacedBy(sp.xs)) {
                            Text("${r.vaccineType} • ${r.productId} • ${sdf.format(Date(r.scheduledAt))}")
                            OutlinedButton(onClick = { vm.markAdministered(r.vaccinationId) }) { Text("Mark Administered") }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(sp.sm))
    }
}
