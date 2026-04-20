/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Rest;

/**
 *
 * @author parth
 */


import EJB.NotificationBeanLocal;
import Entity.Notifications;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationREST {

    @EJB
    private NotificationBeanLocal notificationBean;

    // ── GET all notifications for a user ──────────────────────────
    @GET
    @Path("/user/{userId}")
    public Response getByUser(@PathParam("userId") int userId) {
        Collection<Notifications> list = notificationBean.getNotificationsByUser(userId);
        return Response.ok(list).build();
    }

    // ── GET single notification by ID ─────────────────────────────
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") int id) {
        Notifications n = notificationBean.getNotificationById(id);
        if (n == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(n).build();
    }

    // ── GET unread notifications for a user ───────────────────────
    @GET
    @Path("/user/{userId}/unread")
    public Response getUnread(@PathParam("userId") int userId) {
        return Response.ok(notificationBean.getUnreadNotifications(userId)).build();
    }

    // ── GET read notifications for a user ─────────────────────────
    @GET
    @Path("/user/{userId}/read")
    public Response getRead(@PathParam("userId") int userId) {
        return Response.ok(notificationBean.getReadNotifications(userId)).build();
    }

    // ── GET unread count (badge count) ────────────────────────────
    @GET
    @Path("/user/{userId}/unread/count")
    public Response countUnread(@PathParam("userId") int userId) {
        long count = notificationBean.countUnreadNotifications(userId);
        return Response.ok("{\"unreadCount\":" + count + "}").build();
    }

    // ── GET notifications by type ─────────────────────────────────
    @GET
    @Path("/user/{userId}/type/{type}")
    public Response getByType(@PathParam("userId") int userId,
                              @PathParam("type") String type) {
        return Response.ok(notificationBean.getNotificationsByType(userId, type)).build();
    }

    // ── GET notifications by channel ──────────────────────────────
    @GET
    @Path("/user/{userId}/channel/{channel}")
    public Response getByChannel(@PathParam("userId") int userId,
                                 @PathParam("channel") String channel) {
        return Response.ok(notificationBean.getNotificationsByChannel(userId, channel)).build();
    }

    // ── GET latest N notifications ────────────────────────────────
    @GET
    @Path("/user/{userId}/latest/{limit}")
    public Response getLatest(@PathParam("userId") int userId,
                              @PathParam("limit") int limit) {
        return Response.ok(notificationBean.getLatestNotifications(userId, limit)).build();
    }

    // ── GET all broadcasts ────────────────────────────────────────
    @GET
    @Path("/broadcasts")
    public Response getBroadcasts() {
        return Response.ok(notificationBean.getAllBroadcasts()).build();
    }

    // ── GET all reminders ─────────────────────────────────────────
    @GET
    @Path("/reminders")
    public Response getReminders() {
        return Response.ok(notificationBean.getAllReminders()).build();
    }

    // ── POST send a custom notification ──────────────────────────
    @POST
    @Path("/send")
    public Response send(@QueryParam("userId") int userId,
                         @QueryParam("message") String message,
                         @QueryParam("type") String type,
                         @QueryParam("channel") String channel) {
        notificationBean.sendNotification(userId, message, type, channel);
        return Response.status(Response.Status.CREATED)
                       .entity("{\"message\":\"Notification sent\"}").build();
    }

    // ── POST send event reminder ──────────────────────────────────
    @POST
    @Path("/reminder/{eventId}")
    public Response sendReminder(@PathParam("eventId") int eventId,
                                 @QueryParam("message") String message) {
        notificationBean.sendEventReminder(eventId, message);
        return Response.ok("{\"message\":\"Reminder sent to all confirmed participants\"}").build();
    }

    // ── POST broadcast to all users ───────────────────────────────
    @POST
    @Path("/broadcast")
    public Response broadcast(@QueryParam("message") String message) {
        notificationBean.broadcastToAllUsers(message);
        return Response.ok("{\"message\":\"Broadcast sent to all users\"}").build();
    }

    // ── PUT mark single notification as read ──────────────────────
    @PUT
    @Path("/{id}/read")
    public Response markRead(@PathParam("id") int id) {
        notificationBean.markAsRead(id);
        return Response.ok("{\"message\":\"Marked as read\"}").build();
    }

    // ── PUT mark single notification as unread ────────────────────
    @PUT
    @Path("/{id}/unread")
    public Response markUnread(@PathParam("id") int id) {
        notificationBean.markAsUnread(id);
        return Response.ok("{\"message\":\"Marked as unread\"}").build();
    }

    // ── PUT mark all as read for a user ──────────────────────────
    @PUT
    @Path("/user/{userId}/read-all")
    public Response markAllRead(@PathParam("userId") int userId) {
        notificationBean.markAllAsRead(userId);
        return Response.ok("{\"message\":\"All notifications marked as read\"}").build();
    }

    // ── DELETE a notification ─────────────────────────────────────
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        notificationBean.deleteNotification(id);
        return Response.ok("{\"message\":\"Notification deleted\"}").build();
    }
}



