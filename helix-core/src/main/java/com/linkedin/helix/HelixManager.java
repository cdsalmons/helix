package com.linkedin.helix;

import java.util.List;

import com.linkedin.helix.controller.GenericHelixController;
import com.linkedin.helix.healthcheck.ParticipantHealthReportCollector;
import com.linkedin.helix.participant.HelixStateMachineEngine;
import com.linkedin.helix.participant.StateMachineEngine;
import com.linkedin.helix.spectator.RoutingTableProvider;
import com.linkedin.helix.store.PropertyStore;

/**
 * First class Object any process will interact with<br/>
 * General flow
 * <blockquote>
 * <pre>
 * manager = HelixManagerFactory.getManagerFor<ROLE>(); ROLE can be participant, spectator or a controller<br/>
 * manager.connect();
 * manager.addSOMEListener();
 * manager.start()
 * After start is invoked the subsequent interactions will be via listener onChange callbacks
 * There will be 3 scenarios for onChange callback, which can be determined using NotificationContext.type
 * INIT -> will be invoked the first time the listener is added
 * CALLBACK -> will be invoked due to datachange in the property value
 * FINALIZE -> will be invoked when listener is removed or session expires
 * manager.disconnect()
 * </pre>
 * </blockquote>
 * Default implementations available
 * @see HelixStateMachineEngine for participant
 * @see RoutingTableProvider for spectator
 * @see GenericHelixController  for controller
 * @author kgopalak
 */
public interface HelixManager
{

  /**
   * Start participating in the cluster operations. All listeners will be
   * initialized and will be notified for every cluster state change This method
   * is not re-entrant. One cannot call this method twice.
   *
   * @throws Exception
   */
  void connect() throws Exception;

  /**
   * Check if the connection is alive, code depending on cluster manager must
   * always do this if( manager.isConnected()){ //custom code } This will
   * prevent client in doing anything when its disconnected from the cluster.
   * There is no need to invoke connect again if isConnected return false.
   *
   * @return
   */
  boolean isConnected();

  /**
   * Disconnect from the cluster. All the listeners will be removed and
   * disconnected from the server. Its important for the client to ensure that
   * new manager instance is used when it wants to connect again.
   */
  void disconnect();

  /**
   * @see IdealStateChangeListener#onIdealStateChange(List, NotificationContext)
   * @param listener
   * @throws Exception
   */
  void addIdealStateChangeListener(IdealStateChangeListener listener)
      throws Exception;

  /**
   * @see LiveInstanceChangeListener#onLiveInstanceChange(List,
   *      NotificationContext)
   * @param listener
   */
  void addLiveInstanceChangeListener(LiveInstanceChangeListener listener)
      throws Exception;

  /**
   * @see ConfigChangeListener#onConfigChange(List, NotificationContext)
   * @param listener
   */
  void addConfigChangeListener(ConfigChangeListener listener) throws Exception;

  /**
   * @see MessageListener#onMessage(String, List, NotificationContext)
   * @param listener
   * @param instanceName
   */
  void addMessageListener(MessageListener listener, String instanceName)
      throws Exception;

  /**
   * @see CurrentStateChangeListener#onStateChange(String, List,
   *      NotificationContext)
   * @param listener
   * @param instanceName
   */

  void addCurrentStateChangeListener(CurrentStateChangeListener listener,
      String instanceName, String sessionId) throws Exception;

  /**
   * @see HealthStateChangeListener#onHealthChange(String, List,
   * 		NotificationContext)
   * @param listener
   * @param instanceName
   */
  void addHealthStateChangeListener(HealthStateChangeListener listener,
  		  String instanceName) throws Exception;

  /**
   * @see ExternalViewChangeListener#onExternalViewChange(List,
   *      NotificationContext)
   * @param listener
   */
  void addExternalViewChangeListener(ExternalViewChangeListener listener)
      throws Exception;
  /**
   * Removes the listener. If the same listener was used for multiple changes, all change notifications will be removed.<br/>
   * This will invoke onChange method on the listener with NotificationContext.type set to FINALIZE. Listener can clean up its state.<br/>
   * The data provided in this callback may not be reliable.<br/>
   * When a session expires all listeners will be removed and re-added automatically. <br/>
   * This provides the ability for listeners to either reset their state or do any cleanup tasks.<br/>
   * @param listener
   * @return
   */
  boolean removeListener(Object listener);

//  Generic interface to add a listener
//  void addListener(PropertyType type, Object listener, String... keys);

  /**
   * Return the client to perform read/write operations on the cluster data
   * store
   *
   * @return ClusterDataAccessor
   */
  DataAccessor getDataAccessor();

  /**
   * return the config accessor
   * @return
   */
  ConfigAccessor getConfigAccessor();

  /**
   * Returns the cluster name associated with this cluster manager
   *
   * @return
   */
  String getClusterName();

  /**
   * Returns the instanceName used to connect to the cluster
   *
   * @return
   */

  String getInstanceName();

  /**
   * Get the sessionId associated with the connection to cluster data store.
   */
  String getSessionId();

  /**
   * The time stamp is always updated when a notification is received.
   * This can be used to check if there was any new notification when previous notification was being processed.
   * This is updated based on the notifications from listeners registered.
   */
  long getLastNotificationTime();

  // distributed cluster controller
  /**
   * Add listener for controller change
   */
  void addControllerListener(ControllerChangeListener listener);
  /**
   * Provides admin interface to setup and modify cluster.
   * @return
   */
  HelixAdmin getClusterManagmentTool();

  /**
   * Provide get property store for a cluster
   * @param rootNamespace
   * @param serializer
   * @return
   */
  PropertyStore<ZNRecord> getPropertyStore();

  /**
   * Messaging service which can be used to send cluster wide messages.
   *
   */
  ClusterMessagingService getMessagingService();

  /**
   * Participant only component that periodically update participant health report to cluster
   * manager server.
   *
   */
  ParticipantHealthReportCollector getHealthReportCollector();

  /**
   *
   * @return
   */
  InstanceType getInstanceType();

  /**
   *
   * @return the cluster manager version
   */
  String getVersion();

  /**
   *
   * @return the state machine engine
   */
  StateMachineEngine getStateMachineEngine();

  /**
   *
   * @return true if this is a controller and a leader of the cluster
   */
  boolean isLeader();
}
