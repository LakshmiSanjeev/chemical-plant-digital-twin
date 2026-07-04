/**
 * Provides the simulation layer of the Chemical Plant Digital Twin (CPDT)
 * platform.
 * <p>
 * This package models the physical operation of a chemical plant by
 * simulating industrial sensors, environmental conditions, operational
 * scenarios, and telemetry generation. It serves as the source of
 * real-time process data for the Digital Twin backend.
 * <p>
 * The simulator follows a layered architecture in which scenarios modify
 * the plant environment, sensors observe environmental conditions to
 * produce realistic measurements, and the simulation engine coordinates
 * sensor sampling and telemetry publication.
 *
 * @author Lakshmi Sanjeev
 * @since 1.0
 */

package cpdt.simulator;