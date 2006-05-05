/*
 * Copyright 2006 The Apache Software Foundation or its licensors, as
 * applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef ActiveMQ_MessageConsumerSynchronization_hpp_
#define ActiveMQ_MessageConsumerSynchronization_hpp_

#include "activemq/ISynchronization.hpp"
#include "activemq/command/ActiveMQMessage.hpp"
#include "activemq/command/Message.hpp"
#include "ppr/util/ifr/p"

namespace apache
{
  namespace activemq
  {
    using namespace ifr;
    class MessageConsumer;

/*
 * 
 */
class MessageConsumerSynchronization : public ISynchronization
{
private:
    p<MessageConsumer> consumer ;
    p<Message>         message ;

public:
    MessageConsumerSynchronization(p<MessageConsumer> consumer, p<Message> message) ;
    ~MessageConsumerSynchronization() ;

    virtual void beforeCommit() ;
    virtual void afterCommit() ;
    virtual void afterRollback() ;
} ;

/* namespace */
  }
}

#endif /*ActiveMQ_MessageConsumerSynchronization_hpp_*/
