# Bridges

## What is a bridge?

Bridges are an abstraction desribing a connection to a storage medium, be it a
file, a database, or any other medium we can read appropruate data from.

Both the Source and Destination bridges describe the methods that a storage medium must provide in
order to be consumed by this service.

This abstraction allows us to potentially swap a storage medium for another with minimal code
changes. We could even envision using multiple bridges simultaneously.

Because we want to be able to swap a bridge with another seamlessly, all logic that is tied to that
specific storage medium has to be encapsulated in the bridge itself. Thus, we maintain a good
separation of concerns.
