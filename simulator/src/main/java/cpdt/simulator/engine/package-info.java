/**
 * Provides the core simulation engine responsible for executing the
 * chemical plant simulation.
 *
 * <p>This package coordinates the overall simulation lifecycle,
 * including simulation initialization, periodic device updates,
 * scenario execution, and synchronization with the simulated plant
 * environment. It serves as the central orchestration layer that
 * drives telemetry generation and state transitions throughout the
 * simulator.</p>
 *
 * <p>The engine operates independently of the backend digital twin,
 * focusing solely on plant behavior simulation and telemetry
 * production. Alarm evaluation, persistence, and digital twin
 * management are handled by the backend module.</p>
 */

package cpdt.simulator.engine;